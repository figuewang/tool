package cn.com.yusys.yusp.tool.core.code;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import cn.com.yusys.yusp.tool.core.code.dto.ColumnDto;
import cn.com.yusys.yusp.tool.core.code.dto.TableDto;
import cn.com.yusys.yusp.tool.util.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;


public class YuspWithWorkflowCodeGenerator extends AbstractJavaGenerator {

   

    public YuspWithWorkflowCodeGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
    	FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
    	List<IntrospectedColumn> pks = introspectedTable.getPrimaryKeyColumns();
    	
    	
    	String domainName = table.getDomainObjectName();
    	String domainNameLow = domainName.substring(0,1).toLowerCase()+domainName.substring(1);
    	String domainNameWithPackage = introspectedTable.getBaseRecordType();
    	
    	String pathT = domainNameWithPackage.substring(0,domainNameWithPackage.lastIndexOf("."));
        String basePackage = pathT.substring(0,pathT.lastIndexOf("."));
    	
        String mapName = domainName+"Mapper";
    	String mapNameLow = domainNameLow+"Mapper";
    	String mapNameWithPackage = basePackage+".repository.mapper."+mapName;
    	
    	String serviceName = domainName+"Service";
    	String serviceNameLow = domainNameLow+"Service";     
        String serviceNameWithPackage = basePackage+".service."+serviceName;
        
        String resourceName = domainName+"Resource";
    	String resourceNameLow = domainNameLow+"Resource";     
        String resourceNameWithPackage = basePackage+".web.rest."+resourceName;
        
        /**
         * Service类生成
         */
        CommentGenerator commentGenerator = context.getCommentGenerator();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(serviceNameWithPackage);
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.addImportedType("org.springframework.transaction.annotation.Transactional");
        topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        topLevelClass.addImportedType("org.springframework.stereotype.Service");
        topLevelClass.addImportedType("java.util.List");
        topLevelClass.addImportedType("cn.com.yusys.yusp.commons.mapper.QueryModel");
        topLevelClass.addImportedType("com.github.pagehelper.PageHelper");
        
        topLevelClass.addImportedType(mapNameWithPackage);
        topLevelClass.addImportedType(domainNameWithPackage);
        
        topLevelClass.addAnnotation("@Service");
        topLevelClass.addAnnotation("@Transactional");
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addAnnotation("@Autowired");
        field.setName(mapNameLow);
        FullyQualifiedJavaType mapType = new FullyQualifiedJavaType(mapName);
        field.setType(mapType);
        topLevelClass.addField(field);
                
        String methodName1 = "deleteByPrimaryKey";
        Method method1 = new Method();
        method1.setVisibility(JavaVisibility.PUBLIC);
        method1.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method1.setName(methodName1); 
        String pkStr = "";
        for(IntrospectedColumn pk:pks){
        	FullyQualifiedJavaType pktype = pk.getFullyQualifiedJavaType();           
            Parameter parameter = new Parameter(pktype, pk.getJavaProperty());
            method1.addParameter(parameter);
            pkStr = pkStr + pk.getJavaProperty()+",";
        }	
        if(pkStr.length()>0){
        	pkStr = pkStr.substring(0, pkStr.length()-1);
        }
        method1.addBodyLine("return "+mapNameLow+"."+methodName1+"("+pkStr+");");
        commentGenerator.addGeneralMethodComment(method1, introspectedTable);
        topLevelClass.addMethod(method1);
        
        String methodName2 = "insert";
        Method method2 = new Method();
        method2.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType domain2 = new FullyQualifiedJavaType(domainName);
        method2.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method2.setName(methodName2);                 
        Parameter parameter2 = new Parameter(domain2, "record");
        method2.addParameter(parameter2);         
        method2.addBodyLine("return "+mapNameLow+"."+methodName2+"(record);");
        commentGenerator.addGeneralMethodComment(method2, introspectedTable);
        topLevelClass.addMethod(method2);
        
        String methodName3 = "insertSelective";
        Method method3 = new Method();
        method3.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType domain3 = new FullyQualifiedJavaType(domainName);
        method3.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method3.setName(methodName3);                 
        Parameter parameter3 = new Parameter(domain3, "record");
        method3.addParameter(parameter3);         
        method3.addBodyLine("return "+mapNameLow+"."+methodName3+"(record);");
        commentGenerator.addGeneralMethodComment(method3, introspectedTable);
        topLevelClass.addMethod(method3);
        
        String methodName4 = "selectByPrimaryKey";
        Method method4 = new Method();
        method4.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType type4 = new FullyQualifiedJavaType(domainName);
        method4.setReturnType(type4);
        method4.setName(methodName4); 
        String pkStr4 = "";
        for(IntrospectedColumn pk:pks){
        	FullyQualifiedJavaType pktype = pk.getFullyQualifiedJavaType();           
            Parameter parameter = new Parameter(pktype, pk.getJavaProperty());
            method4.addParameter(parameter);
            pkStr4 = pkStr4 + pk.getJavaProperty()+",";
        }	
        if(pkStr4.length()>0){
        	pkStr4 = pkStr4.substring(0, pkStr4.length()-1);
        }
        method4.addBodyLine("return "+mapNameLow+"."+methodName4+"("+pkStr+");");
        commentGenerator.addGeneralMethodComment(method4, introspectedTable);
        topLevelClass.addMethod(method4);

        String methodName5 = "updateByPrimaryKeySelective";
        Method method5 = new Method();
        method5.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType domain5 = new FullyQualifiedJavaType(domainName);
        method5.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method5.setName(methodName5);                 
        Parameter parameter5 = new Parameter(domain5, "record");
        method5.addParameter(parameter5);         
        method5.addBodyLine("return "+mapNameLow+"."+methodName5+"(record);");
        commentGenerator.addGeneralMethodComment(method5, introspectedTable);
        topLevelClass.addMethod(method5);
        
        String methodName6 = "updateByPrimaryKey";
        Method method6 = new Method();
        method6.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType domain6 = new FullyQualifiedJavaType(domainName);
        method6.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method6.setName(methodName6);                 
        Parameter parameter6 = new Parameter(domain6, "record");
        method6.addParameter(parameter6);         
        method6.addBodyLine("return "+mapNameLow+"."+methodName6+"(record);");
        commentGenerator.addGeneralMethodComment(method6, introspectedTable);
        topLevelClass.addMethod(method6);
        
        String methodName7 = "selectByModel";
        Method method7 = new Method();
        method7.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType re7 = new FullyQualifiedJavaType("List<"+domainName+">");
        method7.setReturnType(re7);
        method7.setName(methodName7);  
        FullyQualifiedJavaType domain7 = new FullyQualifiedJavaType("QueryModel");
        Parameter parameter7 = new Parameter(domain7, "model");
        method7.addParameter(parameter7);   
        method7.addBodyLine("PageHelper.startPage(model.getPage(), model.getSize());");
        method7.addBodyLine("List<"+domainName+"> list = "+mapNameLow+"."+methodName7+"(model);");
        method7.addBodyLine("PageHelper.clearPage();");
        method7.addBodyLine("return list;");
        commentGenerator.addGeneralMethodComment(method7, introspectedTable);
        topLevelClass.addMethod(method7);
        
        if(null!=pks&&!pks.isEmpty()){// 没有主键不生成
	        String methodName8 = "deleteByIds";
	        Method method8 = new Method();
	        method8.setVisibility(JavaVisibility.PUBLIC);
	        method8.setReturnType(FullyQualifiedJavaType.getIntInstance());
	        method8.setName(methodName8);              
        	FullyQualifiedJavaType pktype = pks.get(0).getFullyQualifiedJavaType(); 
        	Parameter parameter8 = new Parameter(pktype, pks.get(0).getJavaProperty()+"s");
            method8.addParameter(parameter8); 
            method8.addBodyLine("return "+mapNameLow+"."+methodName8+"("+pks.get(0).getJavaProperty()+"s"+");");
            commentGenerator.addGeneralMethodComment(method8, introspectedTable);
            topLevelClass.addMethod(method8);
    	}
        
        /**
         * Resource类生成
         */
        FullyQualifiedJavaType resourcetype = new FullyQualifiedJavaType(resourceNameWithPackage);
        TopLevelClass resourceClass = new TopLevelClass(resourcetype);
        resourceClass.addImportedType("org.springframework.web.bind.annotation.RestController");
        resourceClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        resourceClass.addImportedType("org.springframework.web.bind.annotation.RequestMapping");
        resourceClass.addImportedType("org.springframework.web.bind.annotation.GetMapping");
        resourceClass.addImportedType("org.springframework.web.bind.annotation.PostMapping");
        resourceClass.addImportedType("org.springframework.web.bind.annotation.RequestBody");
        resourceClass.addImportedType("org.springframework.web.bind.annotation.PathVariable");
        resourceClass.addImportedType("com.codahale.metrics.annotation.Timed");
        resourceClass.addImportedType("java.util.List");
        resourceClass.addImportedType("cn.com.yusys.yusp.commons.mapper.QueryModel");
        resourceClass.addImportedType("cn.com.yusys.yusp.commons.web.rest.dto.ResultDto");
        resourceClass.addImportedType(serviceNameWithPackage);
        resourceClass.addImportedType(domainNameWithPackage);
            
        resourceClass.addAnnotation("@RestController");
        resourceClass.addAnnotation("@RequestMapping(\"/api/"+domainName.toLowerCase()+"\")");
        resourceClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(resourceClass);
       
        Field field2 = new Field();
        field2.setVisibility(JavaVisibility.PRIVATE);
        field2.addAnnotation("@Autowired");
        field2.setName(serviceNameLow);
        FullyQualifiedJavaType mapType2 = new FullyQualifiedJavaType(serviceName);
        field2.setType(mapType2);
        resourceClass.addField(field2);
        
        String methodName21 = "index";
        Method method21 = new Method();
        method21.setVisibility(JavaVisibility.PROTECTED);
        method21.addAnnotation("@GetMapping(\"/\")");
        method21.addAnnotation("@Timed");
        FullyQualifiedJavaType re21 = new FullyQualifiedJavaType("ResultDto<List<"+domainName+">>");
        method21.setReturnType(re21);
        method21.setName(methodName21);  
        FullyQualifiedJavaType domain21 = new FullyQualifiedJavaType("QueryModel");
        Parameter parameter21 = new Parameter(domain21, "queryModel");
        method21.addParameter(parameter21);
        method21.addBodyLine("List<"+domainName+"> list = "+serviceNameLow+"."+methodName7+"(queryModel);");
        method21.addBodyLine("return new ResultDto<List<"+domainName+">>(list);");
        commentGenerator.addGeneralMethodComment(method21, introspectedTable);
        resourceClass.addMethod(method21);
        
       
        String methodName22 = "show";
        Method method22 = new Method();
        method22.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType type22 = new FullyQualifiedJavaType("ResultDto<"+domainName+">");
        method22.setReturnType(type22);
        method22.setName(methodName22); 
        String pkStr22 = "";
        String pkUrlStr22 = "";
        for(IntrospectedColumn pk:pks){
        	FullyQualifiedJavaType pktype = pk.getFullyQualifiedJavaType();           
            Parameter parameter = new Parameter(pktype, pk.getJavaProperty());
            parameter.addAnnotation("@PathVariable");
            method22.addParameter(parameter);
            pkStr22 = pkStr22 + pk.getJavaProperty()+",";
            pkUrlStr22 = pkUrlStr22 + "/{" + pk.getJavaProperty()+"}";
        }	
        if(pkStr22.length()>0){
        	pkStr22 = pkStr22.substring(0, pkStr22.length()-1);
        }
        method22.addAnnotation("@GetMapping(\""+pkUrlStr22+"\")");
        method22.addAnnotation("@Timed");
        method22.addBodyLine(domainName+" "+domainNameLow +" = "+serviceNameLow+"."+methodName4+"("+pkStr+");");
        method22.addBodyLine("return new ResultDto<"+domainName+">("+domainNameLow+");");
        commentGenerator.addGeneralMethodComment(method22, introspectedTable);
        resourceClass.addMethod(method22);
              
        String methodName23 = "create";
        Method method23 = new Method();
        method23.setVisibility(JavaVisibility.PROTECTED);
        method23.addAnnotation("@PostMapping(\"/\")");
        method23.addAnnotation("@Timed");
        FullyQualifiedJavaType re23 = new FullyQualifiedJavaType("ResultDto<"+domainName+">");
        method23.setReturnType(re23);
        method23.setName(methodName23);  
        FullyQualifiedJavaType domain23 = new FullyQualifiedJavaType(domainName);
        Parameter parameter23 = new Parameter(domain23, domainNameLow);
        parameter23.addAnnotation("@RequestBody");
        method23.addParameter(parameter23);
        method23.addBodyLine(serviceNameLow+"."+methodName2+"("+domainNameLow+");");
        method23.addBodyLine("return new ResultDto<"+domainName+">("+domainNameLow+");");
        commentGenerator.addGeneralMethodComment(method23, introspectedTable);
        resourceClass.addMethod(method23);
        
        String methodName24 = "update";
        Method method24 = new Method();
        method24.setVisibility(JavaVisibility.PROTECTED);
        method24.addAnnotation("@PostMapping(\"/update\")");
        method24.addAnnotation("@Timed");
        FullyQualifiedJavaType re24 = new FullyQualifiedJavaType("ResultDto<Integer>");
        method24.setReturnType(re24);
        method24.setName(methodName24);  
        FullyQualifiedJavaType domain24 = new FullyQualifiedJavaType(domainName);
        Parameter parameter24 = new Parameter(domain24, domainNameLow);
        parameter24.addAnnotation("@RequestBody");
        method24.addParameter(parameter24);
        method24.addBodyLine("int result = "+serviceNameLow+"."+methodName6+"("+domainNameLow+");");
        method24.addBodyLine("return new ResultDto<Integer>(result);");
        commentGenerator.addGeneralMethodComment(method24, introspectedTable);
        resourceClass.addMethod(method24);
            
        String methodName25 = "delete";
        Method method25 = new Method();
        method25.setVisibility(JavaVisibility.PROTECTED);
        FullyQualifiedJavaType re25 = new FullyQualifiedJavaType("ResultDto<Integer>");
        method25.setReturnType(re25);
        method25.setName(methodName25); 
        String pkStr25 = "";
        String pkUrlStr25 = "";
        for(IntrospectedColumn pk:pks){
        	FullyQualifiedJavaType pktype = pk.getFullyQualifiedJavaType();           
            Parameter parameter = new Parameter(pktype, pk.getJavaProperty());
            parameter.addAnnotation("@PathVariable");
            method25.addParameter(parameter);
            pkStr25 = pkStr25 + pk.getJavaProperty()+",";
            pkUrlStr25 = pkUrlStr25 + "/{" + pk.getJavaProperty()+"}";
        }	
        if(pkStr25.length()>0){
        	pkStr25 = pkStr25.substring(0, pkStr25.length()-1);
        }
        method25.addAnnotation("@PostMapping(\"/delete"+pkUrlStr25+"\")");
        method25.addAnnotation("@Timed");
        method25.addBodyLine("int result = "+serviceNameLow+"."+methodName1+"("+pkStr25+");");
        method25.addBodyLine("return new ResultDto<Integer>(result);");
        commentGenerator.addGeneralMethodComment(method25, introspectedTable);
        resourceClass.addMethod(method25);
       
        
        if(null!=pks&&!pks.isEmpty()){// 没有主键不生成
	        String methodName26 = "deletes";
	        Method method26 = new Method();
	        method26.setVisibility(JavaVisibility.PUBLIC);
	        FullyQualifiedJavaType re26 = new FullyQualifiedJavaType("ResultDto<Integer>");
	        method26.setReturnType(re26);
	        method26.setName(methodName26);              
        	FullyQualifiedJavaType pktype = pks.get(0).getFullyQualifiedJavaType(); 
        	Parameter parameter8 = new Parameter(pktype, pks.get(0).getJavaProperty()+"s");
        	parameter8.addAnnotation("@PathVariable");
        	method26.addParameter(parameter8); 
        	method26.addAnnotation("@PostMapping(\"/batchdelete/{"+pks.get(0).getJavaProperty()+"s"+"}\")");
        	method26.addAnnotation("@Timed");
        	method26.addBodyLine("int result = "+serviceNameLow+".deleteByIds("+pks.get(0).getJavaProperty()+"s"+");");
        	method26.addBodyLine("return new ResultDto<Integer>(result);");
        	
            commentGenerator.addGeneralMethodComment(method26, introspectedTable);
            resourceClass.addMethod(method26);
    	}
        
        
        /**
         * 工作流后处理类生成
         */
        String workflowName = domainName+"WorkflowBizAfterProcessor";
        String workflowNameWithPackage = basePackage+".workflow.biz."+workflowName;
        FullyQualifiedJavaType workflowtype = new FullyQualifiedJavaType(workflowNameWithPackage);
        TopLevelClass workflowClass = new TopLevelClass(workflowtype);
        workflowClass.addImportedType("org.slf4j.Logger");
        workflowClass.addImportedType("org.slf4j.LoggerFactory");
        workflowClass.addImportedType("org.springframework.stereotype.Component");
        workflowClass.addImportedType("com.fasterxml.jackson.databind.ObjectMapper");
        workflowClass.addImportedType("cn.com.yusys.yusp.echain.client.consts.Consts");
        workflowClass.addImportedType("cn.com.yusys.yusp.echain.client.message.InstanceMessage");
        workflowClass.addImportedType("cn.com.yusys.yusp.echain.client.message.InstanceMessageProcessor");
       
        workflowClass.addAnnotation("@Component");
        workflowClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(workflowClass);
        FullyQualifiedJavaType superInterface = new FullyQualifiedJavaType("cn.com.yusys.yusp.echain.client.message.InstanceMessageProcessor");
        workflowClass.addSuperInterface(superInterface);
        
        Field log = new Field();
        log.setVisibility(JavaVisibility.PRIVATE);
        log.setName("log");
        FullyQualifiedJavaType logType = new FullyQualifiedJavaType("Logger");
        log.setType(logType);
        log.setFinal(true);
        workflowClass.addField(log);
        log.setInitializationString("LoggerFactory.getLogger(this.getClass())");
        
        Field objectMapper = new Field();
        objectMapper.setVisibility(JavaVisibility.PRIVATE);
        objectMapper.setName("objectMapper");
        FullyQualifiedJavaType objectMapperType = new FullyQualifiedJavaType("ObjectMapper");
        objectMapper.setType(objectMapperType);
        objectMapper.setInitializationString("new ObjectMapper()");
        workflowClass.addField(objectMapper);
        
        String shouldMethodName = "should";
        Method shouldMethod = new Method();
        shouldMethod.setName(shouldMethodName);
        shouldMethod.setVisibility(JavaVisibility.PUBLIC);
        shouldMethod.addAnnotation("@Override");
        FullyQualifiedJavaType parameterShouldType = new FullyQualifiedJavaType("String");
        Parameter parameterShould = new Parameter(parameterShouldType, "wfSign");
        shouldMethod.addParameter(parameterShould);
        shouldMethod.addBodyLine("if(\"修改为流程标识\".equals(wfSign)){");
        shouldMethod.addBodyLine("log.debug(\"后业务处理类命中:\"+this.getClass());");
        shouldMethod.addBodyLine("return true;");
        shouldMethod.addBodyLine("}");
        shouldMethod.addBodyLine("return false;");
        FullyQualifiedJavaType shouldMethodReturnType = new FullyQualifiedJavaType("boolean");
        shouldMethod.setReturnType(shouldMethodReturnType);
        commentGenerator.addGeneralMethodComment(shouldMethod, introspectedTable);
        workflowClass.addMethod(shouldMethod);
       
        String orderMethodName = "order";
        Method orderMethod = new Method();
        orderMethod.setName(orderMethodName);
        orderMethod.setVisibility(JavaVisibility.PUBLIC);
        orderMethod.addAnnotation("@Override");
        orderMethod.addBodyLine("return 0;");
        FullyQualifiedJavaType orderMethodReturnType = new FullyQualifiedJavaType("int");
        orderMethod.setReturnType(orderMethodReturnType);
        commentGenerator.addGeneralMethodComment(orderMethod, introspectedTable);
        workflowClass.addMethod(orderMethod);
        
        String processMethodName = "process";
        Method processMethod = new Method();
        processMethod.setName(processMethodName);
        processMethod.setVisibility(JavaVisibility.PUBLIC);
        processMethod.addAnnotation("@Override");
        FullyQualifiedJavaType exception = new FullyQualifiedJavaType("Exception");
		processMethod.addException(exception);
		FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("String");
        Parameter parameter1 = new Parameter(parameterType, "message");
        processMethod.addParameter(parameter1);
        processMethod.addBodyLine("InstanceMessage instanceMessage = objectMapper.readValue(message, InstanceMessage.class);");
        processMethod.addBodyLine("String messageType = instanceMessage.getType();");
        processMethod.addBodyLine("if(Consts.MESSAGE_TYPE_INIT.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"初始化操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_SUBMIT.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"提交操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_SIGNIN.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"签收操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_SIGNOFF.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"签收取消操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_CHANGE.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"转办操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_JUMP.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"跳转操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_END.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"结束操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_RETURNBACK.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"退回操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_CALLBACK.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"打回操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_TAKEBACK.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"拿回操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_CANCEL.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"撤销操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_HANG.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"挂起操作:\"+message);");
        processMethod.addBodyLine("}else if(Consts.MESSAGE_TYPE_WAKE.equals(messageType)){");
        processMethod.addBodyLine("log.debug(\"唤醒操作:\"+message);");
        processMethod.addBodyLine("}else{");
        processMethod.addBodyLine("log.warn(\"未知操作:\"+message);");
        processMethod.addBodyLine("}");
        
        commentGenerator.addGeneralMethodComment(processMethod, introspectedTable);
        workflowClass.addMethod(processMethod);
        
        
        /**
         * class 添加
         */
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelExampleClassGenerated( topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        if (context.getPlugins().modelExampleClassGenerated( resourceClass, introspectedTable)) {
            answer.add(resourceClass);
        }
        if (context.getPlugins().modelExampleClassGenerated( workflowClass, introspectedTable)) {
            answer.add(workflowClass);
        }
        
        
		// js与html文件生成
		try {
			Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_23);
			freemarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/templates");
			freemarkerConfiguration.setDefaultEncoding("UTF-8");
			freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			Map<String, Object> model = new HashMap<String, Object>();
			
			List<IntrospectedColumn> cols = introspectedTable.getAllColumns();
			TableDto tableDto = new TableDto();
			tableDto.setDomainname(domainName.toLowerCase());
			tableDto.setPkColName(pks.get(0).getJavaProperty());
			List<ColumnDto> colDtos = new ArrayList<ColumnDto>();
			tableDto.setCols(colDtos);
			for(IntrospectedColumn col:cols){
				ColumnDto columnDto = new ColumnDto();
				colDtos.add(columnDto);
				
				columnDto.setProp(col.getJavaProperty());
				String desc = col.getRemarks();
				if(null==desc||"".equals(desc)){
					desc = col.getJavaProperty();
				}
				columnDto.setName(desc);				
				columnDto.setType(getInputType(col.getJdbcType()));
				columnDto.setRequired(!col.isNullable()+"");
			}
			
			model.put("table", tableDto);
			String filepath = this.context.getJavaClientGeneratorConfiguration().getTargetProject();
			
			String htmlCodeWithWorkflow = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate("htmlCodeWithWorkflow.ftl"), model);
			System.out.println(htmlCodeWithWorkflow);
			FileUtil.writeNewContentToFile(filepath+File.separator+tableDto.getDomainname()+"WithWorkflow.html", htmlCodeWithWorkflow);
			
			String jsCodeWithWorkflow = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate("jsCodeWithWorkflow.ftl"), model);
			System.out.println(jsCodeWithWorkflow);
			FileUtil.writeNewContentToFile(filepath+File.separator+tableDto.getDomainname()+"WithWorkflow.js", jsCodeWithWorkflow);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return answer;
    }
    
    private String getInputType(int jdbcType){
		switch (jdbcType) {
			case Types.DECIMAL:
				return "num";
			case Types.NUMERIC:
				return "num";
			case Types.BIGINT:
				return "num";
			case Types.DOUBLE:
				return "num";
			case Types.FLOAT:
				return "num";
			case Types.INTEGER:
				return "num";
			case Types.SMALLINT:
				return "num";
			default:
				return "input";
		}
    }
}
