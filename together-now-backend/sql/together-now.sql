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