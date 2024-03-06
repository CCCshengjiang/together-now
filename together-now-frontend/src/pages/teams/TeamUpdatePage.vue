<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import {showFailToast, showSuccessToast} from "vant";
import myAxios from "../../plugs/myAxios";

const router = useRouter()
const route = useRoute();

const id = route.query.id;

const getTeamData = ref({})

// 获取之前的队伍信息
onMounted(async () => {
  if (id <= 0) {
    showFailToast('加载队伍失败');
    return;
  }
  const res = await myAxios.get('/team/get', {
    params: {
      id,
    }
  });
  if (res?.code === 20000) {
    getTeamData.value = res.data;
    getTeamData.value.teamStatus = String(getTeamData.value.teamStatus);
    result.value = getTeamData.value.expireTime; // 将过期时间赋给 result
  }else {
    showFailToast('加载队伍失败，请刷新重试');
  }
})

const result = ref('');
const showPicker = ref(false);
const onConfirm = ({ selectedValues }) => {
  result.value = selectedValues.join('.');
  showPicker.value = false;
};
const minDate = new Date();

const onSubmit = async () => {
  const postData = {
    ...getTeamData.value,
    status: Number(getTeamData.value.teamStatus),
    expireTime: new Date(result.value),
  }
  const res = await myAxios.post('/team/update', postData)
  if (res?.code === 20000 && res.data) {
    showSuccessToast('更新成功');
    router.push({
      path: '/team',
      replace: true,
    })
  }else {
    showFailToast('更新失败')
  }
};

</script>

<template>
  <div id="teamAddPage">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="getTeamData.teamName"
            name="name"
            label="队伍名称"
            placeholder="请输入队伍名称"
            :rules="[{ required: true, message: '请输入队伍名称' }]"
        />
        <van-field
            v-model="getTeamData.teamProfile"
            rows="3"
            autosize
            label="队伍描述"
            type="textarea"
            placeholder="请输入队伍描述"
        />
        <van-field
            v-model="result"
            is-link
            readonly
            name="datePicker"
            label="过期时间"
            placeholder="点击选择时间"
            @click="showPicker = true"
        />
        <van-popup v-model:show="showPicker" position="bottom">
          <van-date-picker @confirm="onConfirm" @cancel="showPicker = false" :min-date="minDate" />
        </van-popup>
        <van-field name="radio" label="队伍状态">
          <template #input>
            <van-radio-group v-model="getTeamData.teamStatus" direction="horizontal">
              <van-radio name='0'>公开</van-radio>
              <van-radio name='1'>私有</van-radio>
              <div id="secret-row">
                <van-radio name='2'>加密</van-radio>
              </div>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
            v-if="Number(getTeamData.teamStatus) === 2"
            v-model="getTeamData.teamPassword"
            type="password"
            name="password"
            label="队伍密码"
            placeholder="请输入队伍密码"
            :rules="[{ required: true, message: '请填写队伍密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<style scoped>
#secret-row {
  margin-top: 8px;
}
</style>