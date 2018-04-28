package com.roywise.costume.mobile.content.gateway.web;

import com.roywise.common.base.vo.PageVo;
import com.roywise.common.exception.CommonException;
import com.roywise.common.utils.BeanTransferUtils;
import com.roywise.costume.business.content.api.enums.ArticleStatusEnum;
import com.roywise.costume.business.content.api.model.ContentArticle;
import com.roywise.costume.business.content.api.model.ContentVideo;
import com.roywise.costume.business.content.api.service.ContentVideoService;
import com.roywise.costume.business.content.api.vo.ContentArticleVO;
import com.roywise.costume.business.content.api.vo.ContentVideoVO;
import com.roywise.costume.business.util.CostumeUtils;
import com.roywise.mobile.module.comm.api.vo.ErrorMissingParamsMsgVO;
import com.roywise.mobile.module.comm.api.vo.ResultMsgVO;
import com.roywise.mobile.module.comm.api.vo.SuccessMsgWithDataVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(value = "/v1/video/content")
@Validated
public class VideoContentController {
    @Resource
    private ContentVideoService contentVideoService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultMsgVO list( @RequestParam(defaultValue = "0") Long offset, @RequestParam(defaultValue = "10") Long limit)
            throws CommonException {


        limit = limit > 200 ? 200 : limit;

        ContentVideo entity = ContentVideo.builder().state(ArticleStatusEnum.PUSH.toString()).build();

        Map<String, Object> params = contentVideoService.getCondition(entity, offset, limit, "push_time desc");
        List<ContentVideo> dbList = contentVideoService.listPageable(params);

        List<ContentVideoVO> rows = new ArrayList<>();
        for(ContentVideo video :dbList){
            ContentVideoVO vo = BeanTransferUtils.transferColumn(video,ContentVideo.class, ContentVideoVO.class);
            vo.setCoverUrl(CostumeUtils.getUrl(video.getCoverPath()));
            vo.setVideoUrl(CostumeUtils.getUrl(video.getVideoPath()));
            rows.add(vo);
        }

        PageVo<ContentVideoVO> pageVo =  new PageVo<>();
        pageVo.setTotal(contentVideoService.countPageable(params));
        pageVo.setRows(rows);
        pageVo.setLimit(limit);
        pageVo.setOffset(offset);

        return new SuccessMsgWithDataVO(pageVo);
    }

    @RequestMapping(value = "/list_detail", method = RequestMethod.GET)
    public ResultMsgVO listDetail(Long id) throws CommonException {
        if (id == null) {
            return new ErrorMissingParamsMsgVO();
        }

        ContentVideo dbVideo = contentVideoService.selectByPrimaryKey(id);
        ContentVideoVO vo = BeanTransferUtils.transferColumn(dbVideo, ContentVideo.class, ContentVideoVO.class);
        vo.setCoverUrl(CostumeUtils.getUrl(dbVideo.getCoverPath()));
        vo.setVideoUrl(CostumeUtils.getUrl(dbVideo.getVideoPath()));
        return new SuccessMsgWithDataVO(vo);
    }

}
