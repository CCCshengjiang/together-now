import SearchPage from "../pages/users/UserSearchPage.vue";
import UserPage from "../pages/users/UserPage.vue";
import TeamPage from "../pages/teams/TeamPage.vue";
import HomePage from "../pages/HomePage.vue";
import UserEditPages from "../pages/users/UserEditPage.vue";
import SearchUsers from "../pages/users/UserSearchResultPage.vue";
import UserLoginPage from "../pages/users/UserLoginPage.vue";
import TeamAddPage from "../pages/teams/TeamAddPage.vue";

// 定义一些路由
// 每个路由都需要映射到一个组件。
// 我们后面再讨论嵌套路由。
const routes = [
    { path: '/', component: HomePage },
    { path: '/team', component: TeamPage },
    { path: '/user', component: UserPage },
    { path: '/search', component: SearchPage },
    { path: '/user/edit', component: UserEditPages },
    { path: '/user/list', component: SearchUsers },
    { path: '/user/login', component: UserLoginPage },
    { path: '/team/add', component: TeamAddPage }
]

export default routes;