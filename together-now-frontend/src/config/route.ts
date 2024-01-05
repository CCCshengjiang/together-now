import SearchPage from "../pages/SearchPage.vue";
import UserPage from "../pages/UserPage.vue";
import TeamPage from "../pages/TeamPage.vue";
import HomePage from "../pages/HomePage.vue";
import UserEditPages from "../pages/UserEditPage.vue";
import SearchUsers from "../pages/SearchUsersPage.vue";

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
]

export default routes;