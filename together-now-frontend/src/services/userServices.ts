import {getCurrentUserState, setCurrentUserState} from "../states/currentUserState";
import MyAxios from "../plugs/myAxios";

export const getCurrentUser = async () => {
/*    const currentUser = getCurrentUserState();
    if (currentUser) {
        return currentUser;
    }*/
    // 如果当前用户不存在
    const res = await MyAxios.get('/user/current');
    if (res.code === 20000) {
        setCurrentUserState(res.data);
        return res.data;
    }
    return null;
}