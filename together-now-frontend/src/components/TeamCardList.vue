<script setup lang="ts">
import {TeamType} from "../models/team";
import {teamStatusEnum} from "../constants/team";
import myAxios from "../plugs/myAxios";
import {showFailToast, showSuccessToast} from "vant";

interface teamCardListProps {
  teamList: TeamType[];
}

const props = withDefaults(defineProps<teamCardListProps>(), {
      // @ts-ignore
      teamList: [] as TeamType[],
    }
);

const doJoinTeam =async (id: number) => {
  const res = await myAxios.post('/team/join', {
    id: id,
  });
  if (res?.code === 20000) {
    showSuccessToast('加入队伍成功')
  }else {
    showFailToast('加入队伍失败' + (res.description ? `. ${res.description}` : ``))
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
        {{'最大人数' + team.maxNum}}
      </div>
      <div>
        {{'注册时间' + team.expireTime}}
      </div>
      <div>
        {{'创建时间' + team.createTime}}
      </div>
    </template>
    <template #footer>
      <van-button size="small" plain type="primary" @click="doJoinTeam(team.id)">加入队伍</van-button>
    </template>
  </van-card>
</template>

<style scoped>

</style>