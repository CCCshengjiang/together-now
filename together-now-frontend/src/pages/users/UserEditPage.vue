<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../../plugs/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../../services/userServices";
const router = useRouter();
const route = useRoute();
const editUser = ref({
  editKey: route.query.editKey,
  editName: route.query.editName,
  currentValue: route.query.currentValue,
})


const onSubmit = async () => {
  const currentUser = await getCurrentUser();
  if (!currentUser) {
    showFailToast('当前用户未登录');
    return;
  }
  console.log('currentUser', currentUser)
  const res = await myAxios.post('/user/update', {
    'id':currentUser.id,
    [editUser.value.editKey as string]: editUser.value.currentValue,
  });
  console.log('更新请求', res);
  if (res.code === 20000 && res.data > 0) {
    showSuccessToast('更新成功');
    router.back();
  }else {
    showFailToast('更新失败');
  }
};
</script>

<template>
  <van-form @submit="onSubmit">
      <van-field
          v-model="editUser.currentValue"
          :name="`${editUser.editKey}`"
          :label="`${editUser.editName}`"
          :placeholder="`请输入${editUser.editName}`"
          :rules="[{ required: true, message: `请输入${editUser.editName}` }]"
      />
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>

</template>

<style scoped>

</style>