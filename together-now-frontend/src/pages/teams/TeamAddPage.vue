<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";
import {showFailToast, showSuccessToast} from "vant";
import myAxios from "../../plugs/myAxios.ts";

const router = useRouter()

const initFormData = {
  "teamName": "",
  "teamProfile": "",
  "maxNum": 3,
  "expireTime": "",
  "teamStatus": '0',
  "teamPassword": ""
}

const addTeamData = ref({...initFormData})

const result = ref('');
const showPicker = ref(false);
const onConfirm = ({ selectedValues }) => {
  result.value = selectedValues.join('.');
  showPicker.value = false;
};
const minDate = new Date();

const onSubmit = async () => {
  const postData = {
    ...addTeamData.value,
    status: Number(addTeamData.value.teamStatus)
  }
  const res = await myAxios.post("/team/add", postData)
  if (res?.code === 20000 && res.data) {
    showSuccessToast('添加成功');
    router.push({
      path: '/team',
      replace: true,
    })
  }else {
    showFailToast('添加失败')
  }
};
</script>

<template>
  <!-- 文字较长时，通过禁用 scrollable 属性关闭滚动播放 -->
  <van-notice-bar
      text="队伍过期之后系统会自动删除，请根据需要选择合理的过期时间！"
  />
  <div id="teamAddPage">
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="addTeamData.teamName"
            name="name"
            label="队伍名称"
            placeholder="请输入队伍名称"
            :rules="[{ required: true, message: '请输入队伍名称' }]"
        />
        <van-field
            v-model="addTeamData.teamProfile"
            rows="3"
            autosize
            label="队伍描述"
            type="textarea"
            placeholder="请输入队伍描述"
        />
        <van-field name="stepper" label="最大人数">
          <template #input>
            <van-stepper v-model="addTeamData.maxNum" max="20" min="3"/>
          </template>
        </van-field>
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
            <van-radio-group v-model="addTeamData.teamStatus" direction="horizontal">
              <van-radio name="0">公开</van-radio>
              <div id="secret-row">
              <van-radio name="2">加密</van-radio>
              </div>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
            v-if="Number(addTeamData.teamStatus) === 2"
            v-model="addTeamData.teamPassword"
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