<script setup lang="ts">
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../../services/userServices";
import myAxios from "../../plugs/myAxios";
import {showFailToast, showSuccessToast} from "vant";
import {useRouter} from "vue-router";

const user = ref();

onMounted(async () => {
  user.value = await getCurrentUser();
  // 将用户标签添加到activeIds
  activeIds.value = JSON.parse(user.value.tags);
})

const searchText = ref('');
const originTagList = [
  {
    text: '性别',
    children: [
      {text: '男', id: '男'},
      {text: '女', id: '女'},
    ],
  },
  {
    text: '年级',
    children: [
      {text: '大一', id: '大一'},
      {text: '大二', id: '大二'},
      {text: '大三', id: '大三'},
      {text: '大四', id: '大四'},
    ],
  },
  {
    text: '生活',
    children: [
      {text: '种花', id: '种花'},
      {text: '美甲', id: '美甲'},
      {text: '美发', id: '美发'},
      {text: '滑雪', id: '滑雪'},
      {text: '火锅', id: '火锅'},
      {text: '做饭', id: '做饭'},
    ],
  },
  {
    text: '心情',
    children: [
      {text: '开心', id: '开心'},
      {text: '兴奋', id: '兴奋'},
      {text: '幸福', id: '幸福'},
      {text: '平淡', id: '平淡'},
      {text: 'emo中', id: 'emo中'},
      {text: '勿扰', id: '勿扰'},
    ],
  },
  {
    text: '学习方向',
    children: [
      {text: '前端开发', id: '前端开发'},
      {text: '后端开发', id: '后端开发'},
      {text: '全栈开发', id: '全栈开发'},
      {text: '人工智能', id: '人工智能'},
      {text: '深度学习', id: '深度学习'},
      {text: '机器学习', id: '机器学习'},
      {text: '图像识别', id: '图像识别'},
      {text: '算法', id: '算法'},
    ],
  },
  {
    text: '编程语言',
    children: [
      {text: 'Java', id: 'Java'},
      {text: 'Python', id: 'Python'},
      {text: 'C++', id: 'C++'},
      {text: 'C', id: 'C'},
      {text: 'C#', id: 'C#'},
      {text: 'JavaScript', id: 'JavaScript'},
      {text: 'Haskell', id: 'Haskell'},
      {text: 'Rust', id: 'Rust'},
      {text: 'Swift', id: 'Swift'},
      {text: 'matlab', id: 'matlab'},
      {text: 'PHP', id: 'PHP'},
    ],
  },
  {
    text: '年级',
    children: [
      {text: '大一', id: '大一'},
      {text: '大二', id: '大二'},
      {text: '大三', id: '大三'},
      {text: '大四', id: '大四'},
    ],
  },
];

let tagList = ref(originTagList);
/**
 * 搜索过滤
 * @param val
 */

const onSearch = (val) => {
  if (!val.trim()) {
    tagList.value = originTagList; // 如果搜索框为空，则重置列表
  } else {
    tagList.value = originTagList.map(parentTag => {
      const filteredChildren = parentTag.children.filter(item => item.text.includes(val));
      return {...parentTag, children: filteredChildren};
    }).filter(parentTag => parentTag.children.length > 0); // 移除没有子项的父标签
  }
}


const onCancel = () => {
  searchText.value = '';
  tagList.value = originTagList;
}

const activeIds = ref([]);
const activeIndex = ref(0);


const close = (tag) => {
  activeIds.value = activeIds.value.filter(item => {
    return item !== tag;
  })
};

const router = useRouter();

const doEditTags = async () => {
  const tagsValue = JSON.stringify(activeIds.value);
  const res = await myAxios.post('/user/update', {
    'id': user.value.id,
    tags: tagsValue,
  });
  // 省略后续代码
  if (res?.code === 20000) {
    showSuccessToast('修改标签成功');
    await router.push({
      path: '/user',
    })
  } else {
    showFailToast('修改标签失败' + (res.description ? `. ${res.description}` : ``));
  }
}

</script>

<template>
  <form action="/public">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入搜索关键词"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>

  <van-divider content-position="left">我的标签</van-divider>
  <div v-if="activeIds.length === 0">请选择标签，方便找到志同道合的伙伴</div>
  <van-row :gutter="[18, 18]">
    <van-col v-for="tag in activeIds">
      <van-tag closeable size="medium" type="primary" @close="close(tag)">
        {{ tag }}
      </van-tag>
    </van-col>
  </van-row>
  <van-divider content-position="left">选择标签</van-divider>
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />

  <van-button block type="primary" @click="doEditTags" style="margin-bottom: auto">确认修改</van-button>
</template>

<style scoped>

</style>