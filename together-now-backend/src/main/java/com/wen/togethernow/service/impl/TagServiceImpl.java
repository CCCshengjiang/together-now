package com.wen.togethernow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.togethernow.model.domain.Tag;
import com.wen.togethernow.service.TagService;
import com.wen.togethernow.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
 * 标签服务实现类
 *
 * @author wen
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




