import {UserType} from "./user";

export type TeamType = {
    id: number,
    teamName: string,
    teamProfile?: string,
    maxNum: number,
    userId: number,
    teamStatus: string,
    expireTime: Date,
    createTime: Date,
    updateTime: Date,
    captainUser?: UserType,
}