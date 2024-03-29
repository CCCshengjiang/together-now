<script setup lang="ts">
import {TeamType} from "../models/team";
import {teamStatusEnum} from "../constants/team";
import myAxios from "../plugs/myAxios";
import {showDialog, showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../services/userServices.ts";
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";

interface teamCardListProps {
  loading: boolean;
  teamList: TeamType[];
}

const props = withDefaults(defineProps<teamCardListProps>(), {
      loading: true,
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
  if (team.teamStatus === 0) {
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
    location.reload();
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
    location.reload();
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
    location.reload();
  } else {
    showFailToast('解散队伍失败' + (res.description ? `. ${res.description}` : ``))
  }
}

// 定义一个格式化日期时间的方法，只包含年月日
function formatDate(dateString: string) {
  const date = new Date(dateString);
  const year = date.getFullYear(); // 获取年份
  const month = date.getMonth() + 1; // 获取月份，月份从0开始所以+1
  const day = date.getDate(); // 获取日期
  return `${year}年${month}月${day}日`;
}

const showProfile = (userProfile: string, captainEmail: string) => showDialog({
  title: '队伍信息',
  message: userProfile ? (`队伍介绍：${userProfile}` + '\n' + (captainEmail ? `联系队长：${captainEmail}` : '')) : '该队伍暂无介绍',
});

const showProfileNoEmail = (userProfile: string) => showDialog({
  title: '队伍信息',
  message: userProfile ? `队伍介绍：${userProfile}` : '该队伍暂无介绍',
});

</script>

<template>
  <div v-if="loading">
    <!-- 当数据正在加载时，显示3个骨架屏占位 -->
    <van-skeleton title avatar :row="3" v-for="n in 5" :key="n"></van-skeleton>
  </div>
  <div v-else>
    <van-card
        v-for="team in props.teamList"
        :thumb="team?.captainUser?.avatarUrl"
        :desc="team.teamProfile"
        :title="`${team.teamName}`"
        @click-thumb="team.hasJoin ? showProfile(team.teamProfile, team.captainUser?.email) : showProfileNoEmail(team.teamProfile)"
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
          {{ `队伍人数：${team.hasJoinNum} / ${team.maxNum}` }}
        </div>
        <div>
          创建时间：{{ formatDate(team.createTime) }}
        </div>
        <div>
          过期时间：{{ formatDate(team.expireTime) }}
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
    <van-dialog v-model:show="showPasswordDialog" title="队伍密码" show-cancel-button @confirm="doJoinTeam"
                @cancel="doJoinCancel">
      <van-field v-model="password" placeholder="请输入队伍密码"/>
    </van-dialog>
  </div>
</template>

<style scoped>

</style>