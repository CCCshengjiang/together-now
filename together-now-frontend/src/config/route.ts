import SearchPage from "../pages/users/UserSearchPage.vue";
import UserUpdatePage from "../pages/users/UserUpdatePage.vue";
import TeamPage from "../pages/teams/TeamPage.vue";
import FriendsPage from "../pages/FriendsPage.vue";
import UserEditPage from "../pages/users/UserEditPage.vue";
import SearchUsers from "../pages/users/UserSearchResultPage.vue";
import UserLoginPage from "../pages/users/UserLoginPage.vue";
import TeamAddPage from "../pages/teams/TeamAddPage.vue";
import TeamUpdatePage from "../pages/teams/TeamUpdatePage.vue";
import UserPage from "../pages/users/UserPage.vue";
import UserTeamJoinPage from "../pages/teams/UserTeamJoinPage.vue";
import UserTeamCaptainPage from "../pages/teams/UserTeamCaptainPage.vue";
import UserRegisterPage from "../pages/users/UserRegisterPage.vue";
import UserTagsPage from "../pages/users/UserTagsPage.vue";

// 定义一些路由
// 每个路由都需要映射到一个组件。
// 我们后面再讨论嵌套路由。
const routes = [
    { path: '/', title: '搭子组队', component: FriendsPage },
    { path: '/search', title: '寻搭子', component: SearchPage },
    { path: '/user', title: '个人中心', component: UserPage },
    { path: '/user/update', title: '更新信息', component: UserUpdatePage },
    { path: '/user/edit', title: '编辑信息', component: UserEditPage },
    { path: '/user/tags', title: '修改标签', component: UserTagsPage },
    { path: '/user/list', title: '搜索结果', component: SearchUsers },
    { path: '/user/login', title: '用户登录', component: UserLoginPage },
    { path: '/user/register', title: '用户注册', component: UserRegisterPage },
    { path: '/user/team/join', title: '加入的队伍', component: UserTeamJoinPage },
    { path: '/user/team/captain', title: '管理的队伍', component: UserTeamCaptainPage },
    { path: '/team', title: '组队界面', component: TeamPage },
    { path: '/team/add', title: '创建队伍', component: TeamAddPage },
    { path: '/team/update', title: '更新队伍', component: TeamUpdatePage },
]

export default routes;