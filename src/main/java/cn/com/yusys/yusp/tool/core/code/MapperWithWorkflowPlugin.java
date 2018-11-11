
package cn.com.yusys.yusp.tool.core.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import tk.mybatis.mapper.generator.MapperCommentGenerator;

public class MapperWithWorkflowPlugin extends PluginAdapter {

	private boolean caseSensitive = false;
   
    private String beginningDelimiter = "";
    
    private String endingDelimiter = "";
   
    private String schema;
   
    private CommentGeneratorConfiguration commentCfg;
    private boolean forceAnnotation;

    private String queryModel = "cn.com.yusys.yusp.commons.mapper.QueryModel";
    
    @Override
    public void setContext(Context context) {
        super.setContext(context);
        commentCfg = new CommentGeneratorConfiguration();
        commentCfg.setConfigurationType(MapperCommentGenerator.class.getCanonicalName());
        context.setCommentGeneratorConfiguration(commentCfg);
     
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
       
        String caseSensitive = this.properties.getProperty("caseSensitive");
        if (StringUtility.stringHasValue(caseSensitive)) {
            this.caseSensitive = caseSensitive.equalsIgnoreCase("TRUE");
        }
        String forceAnnotation = this.properties.getProperty("forceAnnotation");
        if (StringUtility.stringHasValue(forceAnnotation)) {
            commentCfg.addProperty("forceAnnotation", forceAnnotation);
            this.forceAnnotation = forceAnnotation.equalsIgnoreCase("TRUE");
        }
        String beginningDelimiter = this.properties.getProperty("beginningDelimiter");
        if (StringUtility.stringHasValue(beginningDelimiter)) {
            this.beginningDelimiter = beginningDelimiter;
        }
        commentCfg.addProperty("beginningDelimiter", this.beginningDelimiter);
        String endingDelimiter = this.properties.getProperty("endingDelimiter");
        if (StringUtility.stringHasValue(endingDelimiter)) {
            this.endingDelimiter = endingDelimiter;
        }
        commentCfg.addProperty("endingDelimiter", this.endingDelimiter);
        String schema = this.properties.getProperty("schema");
        if (StringUtility.stringHasValue(schema)) {
            this.schema = schema;
        }
    }

    public String getDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        if (StringUtility.stringHasValue(schema)) {
            nameBuilder.append(schema);
            nameBuilder.append(".");
        }
        nameBuilder.append(beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(endingDelimiter);
        return nameBuilder.toString();
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * map接口添加selectByModel方法
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        interfaze.addImportedType(new FullyQualifiedJavaType(queryModel));
        
        // 添加selectByModel方法
        Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(new FullyQualifiedJavaType("java.util.List<"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+">"));
		method.setName("selectByModel");
		method.addParameter(new Parameter(new FullyQualifiedJavaType(queryModel), "model")); 				
		interfaze.addMethod(method);
        
		//没有主键不生成
		List<IntrospectedColumn> pks = introspectedTable.getPrimaryKeyColumns();
		if(null==pks||pks.isEmpty()){
			return true;
		}else{
			FullyQualifiedJavaType pktype = pks.get(0).getFullyQualifiedJavaType(); 
	    	Parameter parameter8 = new Parameter(pktype, pks.get(0).getJavaProperty()+"s");         
			Method delete = new Method();
			delete.addParameter(parameter8);
			delete.setVisibility(JavaVisibility.PUBLIC);
			delete.setReturnType(new FullyQualifiedJavaType("int"));
			delete.setName("deleteByIds");			
			interfaze.addMethod(delete);
		}
        return true;
    }
    
    /**
     * 处理实体类的包和@Table注解
     *
     * @param topLevelClass
     * @param introspectedTable
     */
    private void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        
        topLevelClass.addImportedType("javax.persistence.*");
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        
        if (StringUtility.stringContainsSpace(tableName)) {
            tableName = context.getBeginningDelimiter()
                    + tableName
                    + context.getEndingDelimiter();
        }
        
        if (caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (StringUtility.stringHasValue(schema)
                || StringUtility.stringHasValue(beginningDelimiter)
                || StringUtility.stringHasValue(endingDelimiter)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if(forceAnnotation){
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        }
    }

    /**
     * 生成基础实体类
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 生成实体类注解KEY对象
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 生成带BLOB字段的对象
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }
    
    /**
     * xml文件增加selectByModel方法
     */
    @Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
    	String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
		
		XmlElement parentElement = document.getRootElement();
		
		XmlElement selectElement = new XmlElement("select");
		selectElement.addAttribute(new Attribute("id","selectByModel"));
		selectElement.addAttribute(new Attribute("parameterType",queryModel));
		selectElement.addAttribute(new Attribute("resultMap","BaseResultMap"));
		parentElement.addElement(selectElement);
    			
		selectElement.addElement(new TextElement("SELECT * FROM "+tableName));		
		XmlElement where = new XmlElement("where");
		selectElement.addElement(where);				
		List<IntrospectedColumn> list = introspectedTable.getAllColumns();		
		for(IntrospectedColumn column:list){
			XmlElement ife = new XmlElement("if");
			String testValue = "condition."+column.getJavaProperty()+" !=null and condition."+column.getJavaProperty()+" !=''";
			ife.addAttribute(new Attribute("test",testValue));				
				XmlElement choose = new XmlElement("choose");				
					XmlElement when = new XmlElement("when");
					//<when test="@cn.com.yusys.yusp.commons.mapper.provider.OGNLUtil@like(condition.id"> AND ID like #{condition.id,javaType=VARCHAR }</when>
						StringBuilder whenTestP = new StringBuilder();
						whenTestP.append("@cn.com.yusys.yusp.commons.mapper.provider.OGNLUtil@like(condition.");
						whenTestP.append(column.getJavaProperty());
						whenTestP.append(")");
						when.addAttribute(new Attribute("test",whenTestP.toString()));
						
						StringBuilder whenTestV = new StringBuilder();
						whenTestV.append(" AND ");
						whenTestV.append(column.getActualColumnName());
						whenTestV.append(" LIKE #{condition.");
						whenTestV.append(column.getJavaProperty());
						whenTestV.append(",jdbcType=");
						whenTestV.append(column.getJdbcTypeName());
						whenTestV.append("}");
						when.addElement(new TextElement(whenTestV.toString()));
					XmlElement otherwise = new XmlElement("otherwise");				
						StringBuilder otherwiseV = new StringBuilder();
						otherwiseV.append("AND ");
						otherwiseV.append(column.getActualColumnName());
						otherwiseV.append(" = #{condition.");
						otherwiseV.append(column.getJavaProperty());
						otherwiseV.append(",jdbcType=");
						otherwiseV.append(column.getJdbcTypeName());
						otherwiseV.append("}");
						otherwise.addElement(new TextElement(otherwiseV.toString()));
					choose.addElement(when);
					choose.addElement(otherwise);
				ife.addElement(choose);
			where.addElement(ife);
		}
		where.addElement(new TextElement("${dataAuth}"));
		XmlElement sort = new XmlElement("if");
		sort.addAttribute(new Attribute("test","sort != null"));
		sort.addElement(new TextElement("order by ${sort}"));
		selectElement.addElement(sort);
		
		List<IntrospectedColumn> pks = introspectedTable.getPrimaryKeyColumns();
		if(null==pks||pks.isEmpty()){//无主键不生成
			return true;
		}else{
			IntrospectedColumn pkCol = pks.get(0);
			// 多主键删除
			XmlElement deleteElement = new XmlElement("delete");
			deleteElement.addAttribute(new Attribute("id","deleteByIds"));
			deleteElement.addAttribute(new Attribute("parameterType","java.lang.String"));
			parentElement.addElement(deleteElement);
			deleteElement.addElement(new TextElement("DELETE FROM "+tableName+" WHERE "+pkCol.getActualColumnName()+" IN"));
				XmlElement foreach = new XmlElement("foreach");
				foreach.addAttribute(new Attribute("collection","@cn.com.yusys.yusp.commons.mapper.provider.OGNLUtil@toList('java.lang.String',_parameter)"));
				foreach.addAttribute(new Attribute("open","("));
				foreach.addAttribute(new Attribute("close",")"));
				foreach.addAttribute(new Attribute("separator",","));
				foreach.addAttribute(new Attribute("item","id"));
				foreach.addElement(new TextElement("#{id,jdbcType=VARCHAR}"));
							
			deleteElement.addElement(foreach);
			//deleteElement.addElement(new TextElement("${dataAuth}"));
		}
    	return true;
    }

    @Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		
		Context contextNow = introspectedTable.getContext();
		YuspWithWorkflowCodeGenerator opClassGenerator = new YuspWithWorkflowCodeGenerator();
        opClassGenerator.setContext(contextNow);
        opClassGenerator.setIntrospectedTable(introspectedTable);
        List<CompilationUnit> classExt = opClassGenerator.getCompilationUnits();
        
        List<GeneratedJavaFile> javaFiles = new ArrayList<GeneratedJavaFile>();
        for(CompilationUnit unit:classExt){
        	GeneratedJavaFile gjft = new GeneratedJavaFile(
        			unit,
    				contextNow.getJavaModelGeneratorConfiguration().getTargetProject(),
    				contextNow.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
    				contextNow.getJavaFormatter());
        	javaFiles.add(gjft);
        }		
		return javaFiles;
    }
    
}
