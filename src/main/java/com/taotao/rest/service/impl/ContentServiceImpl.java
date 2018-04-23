package com.taotao.rest.service.impl;

import java.util.List;

import com.taotao.rest.annotation.MyCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ContentService;

import sun.tools.jar.resources.jar;

/**
 * 内容管理
 * <p>Title: ContentServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.com</p> 
 * @author	入云龙
 * @date	2015年9月8日下午3:03:28
 * @version 1.0
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
//	@Value("${INDEX_CONTENT_REDIS_KEY}")
//	private String INDEX_CONTENT_REDIS_KEY;
	private static final String INDEX_CONTENT_REDIS_KEY = "INDEX_CONTENT_REDIS_KEY"; //修改为常量
	
	@Override
	@MyCache(cacheName = INDEX_CONTENT_REDIS_KEY,key="#contentCid") //加入这一行注解，其中key为EL表达式，实际值为形参contentCid的值
	public List<TbContent> getContentList(Long contentCid) { //由于LocalVariableTableParameterNameDiscoverer无法获取类型为原始类型long.int等形参的形参名，所以使用包装类替换
		//根据内容分类id查询内容列表
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(contentCid);
		//执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		return list;
	}
}
