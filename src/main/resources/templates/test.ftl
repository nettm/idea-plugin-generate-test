package test.${package};

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.common.BaseTest;
import java.util.List;

<#list imports as import>
import ${import};
</#list>

public class ${className}Test extends BaseTest {

    @Autowired
    private ${interfaceName} testService;

    <#list methods as method>
    @Test
    public void ${method.name}() throws Exception {
    <#list method.parameterList as parameter>
        ${parameter.value} ${parameter.key} = new ${parameter.value}();
    </#list>

        ${method.returnType} result = testService.${method.name}(${method.parameters});
        System.out.println(JSONObject.toJSONString(result));
        Assert.assertNotNull(result);
    }

    </#list>
}
