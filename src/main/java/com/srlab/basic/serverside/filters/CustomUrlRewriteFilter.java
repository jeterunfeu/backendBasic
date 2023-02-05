package com.srlab.basic.serverside.filters;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

//@Order(3)
@Component
public class CustomUrlRewriteFilter extends UrlRewriteFilter {

//    private final Logger LOG = LoggerFactory.getLogger(CustomUrlRewriteFilter.class);
//
//    private UrlRewriter urlRewriter;
//
//    @Autowired
//    Environment env;
//
//    @Override
//    public void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
//        LOG.info("rewriter start");
//        try {
//            ClassPathResource classPathResource = new ClassPathResource("urlrewrite.xml");
//            InputStream inputStream = classPathResource.getInputStream();
//            Conf conf1 = new Conf(filterConfig.getServletContext(), inputStream, "urlrewrite.xml", "");
//            urlRewriter = new UrlRewriter(conf1);
//        } catch (Exception e) {
//            throw new ServletException(e);
//        }
//    }
//
//    @Override
//    public UrlRewriter getUrlRewriter(ServletRequest request, ServletResponse response, FilterChain chain) {
//        return urlRewriter;
//    }
//
//    @Override
//    public void destroyUrlRewriter() {
//        if (urlRewriter != null)
//            urlRewriter.destroy();
//    }
}
