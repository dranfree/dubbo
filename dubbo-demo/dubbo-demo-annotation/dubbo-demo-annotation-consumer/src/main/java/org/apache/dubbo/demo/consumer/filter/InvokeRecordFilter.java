package org.apache.dubbo.demo.consumer.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

/**
 * 记录每次调用的服务提供方URL信息
 *
 * @author ran.ding
 * @since 2021/11/17
 */
@Activate(group = CommonConstants.CONSUMER)
public class InvokeRecordFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(InvokeRecordFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        logger.info("invoke: " + invoker.getUrl());
        return invoker.invoke(invocation);
    }
}
