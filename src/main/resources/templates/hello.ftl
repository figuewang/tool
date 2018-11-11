<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="./element/index.css">
  <style type="text/css"> 
    #label {
        color:white;
    }
  </style>
</head>
<body>
  <div id="app">
    <el-container class="wrapper">
        <el-header
          height="75px"
          :style="{'background-color':head.color}">   
          <el-carousel indicator-position="none" height='75px'>
                <el-carousel-item v-for="item in labels" :key="item">
                    <h2 id="label">{{ item }}</h2>
                </el-carousel-item>
          </el-carousel>   
        </el-header>
        <el-container>
          <el-aside class="menu">
            <el-menu @select="menuItemSelect">
              <el-menu-item index="0">
                <i class="el-icon-tickets"></i>
                <span style="color:#409eff" slot="title">{{pages[0].name}}</span>
              </el-menu-item>
              <el-menu-item index="1">
                <i class="el-icon-loading"></i>
                <span style="color:#409eff" slot="title">{{pages[1].name}}</span>
              </el-menu-item>
              <el-menu-item index="2">
                <i class="el-icon-view"></i>
                <span style="color:#409eff" slot="title">{{pages[2].name}}</span>
              </el-menu-item>
            </el-menu>
          </el-aside>
          <el-main class="content">        
            <div v-show="showIndex">
<h1>基础环境:</h1>  
<h5>jdk1.8、数据库、redis</h5>
<h5>YuIDE.exe </h5>

<h1>微服务环境搭建：</h1>     	
<h5>打开IDE,maven私库配置</h5>
<h5>后端环境搭建【包含 注册中心、统一登录认证、网关、示例微服务】</h5>
<h5>修改application-dev.yml中数据库与redis配置</h5>
<h5>依次启动服务</h5>
<h5>示例微服务上后端代码自动构造</h5>
<h5>swagger测试</h5>

<h1>简单的后台开发：</h1>     	
<h5>以表查询为例，开发一个后端查询接口</h5>
</div>
            <div  v-show="showCode">
            	<el-form :inline="true" class="demo-form-inline">
  					<el-form-item label="表名">
    					<el-input v-model="tableName" placeholder="输入表名查询"></el-input>
  					</el-form-item>
  					<el-form-item>
    					<el-button type="primary" @click="loadData">查询</el-button>
    					<el-button type="primary" @click="clear">重置</el-button>
  					</el-form-item>
				</el-form>
            	<el-table ref="table" 
            		:data="tableData" border style="width:100%" height="380"
            		:highlight-current-row="highlight"
            		v-loading="loadingOpen"
    				element-loading-text="拼命加载中"
   					element-loading-spinner="el-icon-loading"
    				element-loading-background="#FFFFFF">
    				
            		<el-table-column prop="name" label="表名"></el-table-column>
            		<el-table-column prop="remarks" label="描述"></el-table-column>
            		<el-table-column label="代码生成" width="420px">
      					<template slot-scope="scope">
        					<el-button
          						size="mini"
          						type="primary"
          						@click="downloadYusys(scope.$index, scope.row)">增删查改</el-button>
        					<el-button
          						size="mini"
          						type="primary"
          						@click="downloadYusysWorkflow(scope.$index, scope.row)">增删查改(含流程)</el-button>
          					<el-button
          						size="mini"
          						type="primary"
          						@click="downloadBase(scope.$index, scope.row)">原生Mybatis</el-button>
      					</template>
    				</el-table-column>
    				<el-table-column label="数据导出">
      					<template slot-scope="scope">
          					<el-button
          						size="mini"
          						type="primary"
          						@click="exportData(scope.$index, scope.row)">导出数据</el-button>	
      					</template>
    				</el-table-column>
            	</el-table>
            	<el-pagination
     				@size-change="sizeChange"
      				@current-change="pageChange"
      				:current-page="page"
      				:page-sizes="[10,20]"
      				layout="total, sizes, prev, pager, next, jumper"
      				:total="total">
    			</el-pagination>
    			<el-card>
    				<el-input
					  type="textarea"
					  :rows="2"
					  v-model="logTxt">
					</el-input>
				</el-card>
    			<el-dialog title="java包名称" :visible.sync="dialogFormVisible">
  					<el-form :model="form" :rules="rules" ref="ruleForm">
  						<el-form-item label="表名" label-width="100px" prop="tableName">
      						<el-input v-model="form.tableName" auto-complete="off"></el-input>
    					</el-form-item>
    					<el-form-item label="包名" label-width="100px" prop="packageName">
      						<el-input v-model="form.packageName" auto-complete="off"></el-input>
    					</el-form-item>
  					</el-form>
  					<div slot="footer" class="dialog-footer">
    					<el-button @click="dialogFormVisible = false">取 消</el-button>
    					<el-button type="primary" @click="download">确 定</el-button>
  					</div>
				</el-dialog>
            </div>
            <div  v-show="showAdmin">
            	
            </div>
          </el-main>
        </el-container>
    </el-container>
  </div>
</body>
  <script src="./lib/vue.js"></script>
  <script src="./element/index.js"></script>
  <script src="./lib/vue-router.js"></script>
  <script src="./lib/sockjs.min.js"></script>
  <script src="./lib/stomp.min.js"></script>
  <script src="./lib/jquery-2.0.2.min.js"></script>
  <script>
    new Vue({
      el: '#app',
      data: function() {
        return { 
            head:{
                color:'#409eff'
            },
            pages:[
            	{name:'环境搭建',url:'index'},
            	{name:'在线代码生成',url:'code'},
            	{name:'监控',url:'admin'}
            ],
            labels:['数据 增、删、查、改等 前端代码 后端代码 一键生成','全局序列号服务','分布式定时调度服务','分布式文件服务','统一认证服务','网关服务【路由、限流、报文注入等】','工作流服务','消息服务','微服务编排、监控'],
            showIndex:true,
            showCode:false,
            showAdmin:false,
            tableData:[],
            tableName:'',
            loadingOpen:false,
            highlight:true,
            total:0,
            page:1,
            size:10,
            dialogFormVisible:false,
            form:{
            	tableName:'',
            	packageName:''
            },
            downloadUrl:'',
            rules:{
            	tableName: [ { required: true, message: '请输入表名' }],
            	packageName: [ { required: true, message: '请输入包名' }]
            },
            webSocketClient: null,
            logTxt:''
        }
      },
      methods: { 
        menuItemSelect:function(key) {
        	var _self = this;
        	if(key=='0'){
        		_self.showIndex = true;
        		_self.showCode = false;
        		_self.showAdmin = false;
        	}else if(key=='1'){
        		_self.showIndex = false;
        		_self.showCode = true;
        		_self.showAdmin = false;
        	}else if(key=='2'){
        		_self.admin();
        	}
        },
        sizeChange: function(val) {
        	this.size = val;
            var _self = this;
      		this.loadData(_self.page,_self.size,_self.tableName);
        },
   		pageChange: function(val) {
            this.page = val;
            var _self = this;
      		this.loadData(_self.page,_self.size,_self.tableName);
        },
        loadData:function(page,size,tableName){
        	var _self = this;
        	_self.loadingOpen = true;
        	$.ajax({ url:'api/tool/allTable?page='+this.page+'&size='+this.size+'&tableName='+this.tableName, type:'GET', async:true, timeout:5000,dataType:'json',success:function(data,textStatus,jqXHR){
        	
        	_self.tableData=data.data;
        	_self.total = data.total;
        	console.info(data);
        	_self.loadingOpen = false;
        	
        	},error:function(xhr,textStatus){
        		_self.loadingOpen = false;
        	}});
        },
        clear:function(){
        	this.tableName='';
        },
        downloadYusys:function(index,row){
        	this.form.tableName = row.name;
        	this.downloadUrl = 'yuspcode';
        	this.dialogFormVisible = true;
        },
        downloadBase:function(index,row){
        	this.form.tableName = row.name;
        	this.downloadUrl = 'code';
        	this.dialogFormVisible = true;
        },
        downloadYusysWorkflow:function(index,row){
        	this.form.tableName = row.name;
        	this.downloadUrl = 'yuspworkflowcode';
        	this.dialogFormVisible = true;
        },
        exportData:function(index,row){
        	var _self = this;
        	var tableNameT = row.name;
            $.ajax({ url:'api/tool/export?tableName='+tableNameT, type:'GET', async:true, timeout:5000,dataType:'json',success:function(data,textStatus,jqXHR){
            	_self.$message({message:'导出发起成功,查看控制台',type:'success'});     	
        	},error:function(xhr,textStatus){
        		_self.$message('导出发起失败');
        	}});
        },
        download:function(){
        	var _self = this;
        	this.$refs['ruleForm'].validate((valid) => {
          		if (valid) {
          			this.dialogFormVisible = false;
          			window.location=_self.downloadUrl+'?tableName='+_self.form.tableName+'&packageName='+_self.form.packageName;
          		} else {
            		return false;
          		}
        	});
        },
        admin:function(){
        	window.open('/admin');
        },
        connect:function () {
        	var _self = this;
    		var socket = new SockJS("/myWebSocket");//如果前后端分离项目需要拼接具体地址，前后端分离index.html可放在
    		_self.webSocketClient = Stomp.over(socket);
    		_self.webSocketClient.connect({},_self.listen);
		},
		listen:function(frame) {
			var _self = this;
    		_self.webSocketClient.subscribe('/topic/ip', function (body) {  
    			var messageT = body.body.toString();
    			_self.logTxt= messageT;
    			if(messageT.indexOf("done") != -1){
          			window.location='/downloadSqlFile?path='+messageT.substring(4);
    			}
    			console.log(body);
    		});
		}
      },
      mounted: function(){
      	var _self = this;
      	_self.connect();
      }
    })   
  </script>
</html>