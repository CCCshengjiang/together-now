<script setup lang="ts">
import {TeamType} from "../models/team";
import {teamStatusEnum} from "../constants/team";

interface teamCardListProps {
  teamList: TeamType[];
}

const props = withDefaults(defineProps<teamCardListProps>(), {
      // @ts-ignore
      teamList: [] as TeamType[],
    }
);
</script>

<template>
  <van-card
      v-for="team in props.teamList"
      :thumb="team.captainUser.avatarUrl"
      :desc="team.teamProfile"
      :title="`${team.teamName}`"
  >
    <template #tags>
      <van-tag plain type="primary" style="margin-right: 8px; margin-top: 8px">
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
      <van-button size="small">加入队伍</van-button>
    </template>
  </van-card>
</template>

<style scoped>

</style>