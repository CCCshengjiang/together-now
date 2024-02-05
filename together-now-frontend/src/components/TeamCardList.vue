<script setup lang="ts">
import {TeamType} from "../models/team";
import {teamStatusEnum} from "../constants/team";
import myAxios from "../plugs/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../services/userServices.ts";
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";

interface teamCardListProps {
  teamList: TeamType[];
}

const props = withDefaults(defineProps<teamCardListProps>(), {
      // @ts-ignore
      teamList: [] as TeamType[],
    }
);

const showPasswordDialog = ref(false);
const password = ref('');
const curTeamId = ref();

const doJoinCancel = () => {
  curTeamId.value = 0;
  password.value = '';
}

const preJoinTeam = (team: TeamType) => {
  curTeamId.value = team.id;
  if (team.teamStatus === teamStatusEnum['0']) {
    doJoinTeam();
  } else {
    showPasswordDialog.value = true;
  }
}

const doJoinTeam = async () => {
  if (!curTeamId.value) {
    return;
  }
  const res = await myAxios.post('/team/join', {
    id: curTeamId.value,
    teamPassword: password.value,
  });
  if (res?.code === 20000) {
    showSuccessToast('加入队伍成功');
    doJoinCancel();
  } else {
    showFailToast('加入队伍失败' + (res.description ? `. ${res.description}` : ``));
  }
}

const currentUser = ref();
onMounted(async () => {
  currentUser.value = await getCurrentUser();
})

const router = useRouter();
const doUpdateTeam = (id: number) => {
  router.push({
    path: '/team/update',
    query: {
      id,
    }
  })
}

const doQuitTeam = async (id: number) => {
  const res = await myAxios.post('/team/quit', {
    id,
  });
  if (res?.code === 20000) {
    showSuccessToast('退出队伍成功')
  } else {
    showFailToast('退出队伍失败' + (res.description ? `. ${res.description}` : ``))
  }
}

const doDisbandTeam = async (id: number) => {
  const res = await myAxios.post('/team/disband', {
    id,
  });
  if (res?.code === 20000) {
    showSuccessToast('解散队伍成功')
  } else {
    showFailToast('解散队伍失败' + (res.description ? `. ${res.description}` : ``))
  }
}

</script>

<template>
  <van-card
      v-for="team in props.teamList"
      :thumb="team.captainUser.avatarUrl"
      :desc="team.teamProfile"
      :title="`${team.teamName}`"
  >
    <template #tags>
      <van-tag plain v-if="Number(team.teamStatus) === 0" type="primary" style="margin-right: 8px; margin-top: 8px">
        {{ teamStatusEnum[team.teamStatus] }}
      </van-tag>
      <van-tag plain v-if="Number(team.teamStatus) === 1" type="success" style="margin-right: 8px; margin-top: 8px">
        {{ teamStatusEnum[team.teamStatus] }}
      </van-tag>
      <van-tag plain v-if="Number(team.teamStatus) === 2" type="danger" style="margin-right: 8px; margin-top: 8px">
        {{ teamStatusEnum[team.teamStatus] }}
      </van-tag>
    </template>
    <template #bottom>
      <div>
        {{ '最大人数' + team.maxNum }}
      </div>
      <div>
        {{ '过期时间' + team.expireTime }}
      </div>
      <div>
        {{ '创建时间' + team.createTime }}
      </div>
    </template>
    <template #footer>
      <van-button size="small" v-if="team.hasJoin === false" plain type="primary"
                  @click="preJoinTeam(team)">加入队伍
      </van-button>
      <van-button size="small" v-if="team.userId === currentUser?.id" plain type="success"
                  @click="doUpdateTeam(team.id)">更新队伍
      </van-button>
      <van-button size="small" v-if="team.hasJoin === true" plain type="warning"
                  @click="doQuitTeam(team.id)">退出队伍
      </van-button>
      <van-button size="small" v-if="team.userId === currentUser?.id" plain type="danger"
                  @click="doDisbandTeam(team.id)">解散队伍
      </van-button>
    </template>
  </van-card>
  <van-dialog v-model:show="showPasswordDialog" title="队伍密码" show-cancel-button @confirm="doJoinTeam" @cancel="doJoinCancel">
    <van-field v-model="password" placeholder="请输入队伍密码"/>
  </van-dialog>

</template>

<style scoped>

</style>