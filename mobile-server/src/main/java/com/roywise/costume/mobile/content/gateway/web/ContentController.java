package com.roywise.costume.mobile.content.gateway.web;

import com.roywise.common.base.vo.PageVo;
import com.roywise.common.exception.CommonException;
import com.roywise.common.utils.BeanTransferUtils;
import com.roywise.costume.business.content.api.enums.ArticleStatusEnum;
import com.roywise.costume.business.content.api.enums.ArticleTypeEnum;
import com.roywise.costume.business.content.api.model.ContentArticle;
import com.roywise.costume.business.content.api.service.ContentArticleService;
import com.roywise.costume.business.content.api.vo.ContentArticleVO;
import com.roywise.costume.business.util.CostumeUtils;
import com.roywise.mobile.module.comm.api.vo.ErrorMissingParamsMsgVO;
import com.roywise.mobile.module.comm.api.vo.ErrorValidateMsgVO;
import com.roywise.mobile.module.comm.api.vo.ResultMsgVO;
import com.roywise.mobile.module.comm.api.vo.SuccessMsgWithDataVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.EnumUtils;
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

/**
 * @description:
 * @date: 2018/3/23 11:53
 */
@Log4j2
@RestController
@RequestMapping(value = "/v1/content/info")
@Validated
public class ContentController {

    @Resource
    private ContentArticleService contentArticleService;


    private static final String CACHE_ARTICE_LIST = "cache-artices-";

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultMsgVO list(String articleType, @RequestParam(defaultValue = "0") Long offset, @RequestParam(defaultValue = "10") Long limit)
            throws CommonException {
        if (StringUtils.isBlank(articleType)) {
            return new ErrorMissingParamsMsgVO();
        }

        if (!EnumUtils.isValidEnum(ArticleTypeEnum.class, articleType)) {
            return new ErrorValidateMsgVO("articleType must be:'COSTUME','SHIRT','SUIT','SLACKS'");
        }

        limit = limit > 200 ? 200 : limit;

        if (ArticleTypeEnum.COSTUME.toString().equals(articleType)) {
            articleType = null;
        }
        ContentArticle entity = ContentArticle.builder().type(articleType).state(ArticleStatusEnum.PUSH.toString()).build();

        Map<String, Object> params = contentArticleService.getCondition(entity, offset, limit, "push_time desc");
        List<ContentArticle> dbList = contentArticleService.listPageable(params);

        List<ContentArticleVO> rows = new ArrayList<>();
        for(ContentArticle article :dbList){
            ContentArticleVO vo = BeanTransferUtils.transferColumn(article,ContentArticle.class, ContentArticleVO.class);
            vo.setCoverImgUrl(CostumeUtils.getUrl(vo.getCoverImgUrl()));
            rows.add(vo);
        }

        PageVo<ContentArticleVO> pageVo =  new PageVo<>();
        pageVo.setTotal(contentArticleService.countPageable(params));
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

        ContentArticle dbArticle = contentArticleService.selectByPrimaryKey(id);
        ContentArticleVO vo = BeanTransferUtils.transferColumn(dbArticle, ContentArticle.class, ContentArticleVO.class);
        vo.setCoverImgUrl(CostumeUtils.getUrl(dbArticle.getCoverImgUrl()));

        return new SuccessMsgWithDataVO(vo);
    }
}
