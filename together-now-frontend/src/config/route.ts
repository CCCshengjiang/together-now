import Home from "../pages/Home.vue";
import Team from "../pages/Team.vue";
import User from "../pages/User.vue";

// 定义一些路由
// 每个路由都需要映射到一个组件。
// 我们后面再讨论嵌套路由。
const routes = [
    { path: '/', component: Home },
    { path: '/team', component: Team },
    {path: '/user', component: User}
]

export default routes;