# 创建together_now数据库
create database if not exists together_now;
use together_now;

# 创建用户表
drop table if exists user;
create table user
(
    id            bigint auto_increment comment 'id'
        primary key,
    username      varchar(256)                       null comment '用户名',
    avatar_url    varchar(1024)                      null comment '头像',
    gender        tinyint                            null comment '性别',
    user_account  varchar(512)                       null comment '账号',
    user_password varchar(512)                       null comment '密码',
    phone         varchar(128)                       null comment '电话',
    email         varchar(512)                       null comment '邮箱',
    user_status   int      default 0                 null comment '状态',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 null comment '是否删除',
    user_role     int      default 0                 not null comment '用户角色，0-普通用户，1-管理员',
    id_code       varchar(512)                       null comment '编号',
    tags          varchar(1024)                      null comment '用户标签'
)
    comment '用户';

# 创建队伍表
drop table if exists team;
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    team_name        varchar(256)                       null comment '队伍名称',
    team_profile varchar(1024)                      null comment '队伍描述',
    max_num     int      default 1                 null comment '最大人数',
    expire_time datetime                           null comment '过期时间',
    user_id     bigint                             null comment '队长的id',
    team_status      int      default 0                 not null comment '状态，0-公开，1-私有，2-加密',
    team_password    varchar(512)                       null comment '队伍密码',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 null comment '是否删除'
)
    comment '队伍表';

# 创建用户-队伍关系表表
drop table if exists team;
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    user_id     bigint comment '用户id',
    team_id     bigint comment '队伍id',
    join_time   datetime                           null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 null comment '是否删除'
)
    comment '用户-队伍关系表';

# 创建标签表
drop table if exists tag;
create table tag
(
    id          bigint auto_increment comment 'id'
        primary key,
    tag_name    varchar(256)                       null comment '标签名称',
    user_id     bigint                             null comment '用户id',
    parent_id   bigint                             null comment '父标签id',
    is_parent   tinyint                            null comment '1 - 父标签，0 - 不是',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 null comment '是否删除',
    constraint uniIdx_tagName
        unique (tag_name)
)
    comment '标签';
# 给用户id添加普通索引
create index idx_userId
    on tag (user_id);