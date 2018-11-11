<div>
    <el-form-q :field-data="queryFields" :buttons="queryButtons"></el-form-q>
    <div class="yu-toolBar">
        <el-button-group>
            <el-button icon="plus" @click="addFn">新增</el-button>
            <el-button icon="edit" @click="modifyFn">修改</el-button>
            <el-button icon="document" @click="infoFn">详情</el-button>
            <el-button icon="yx-bin" @click="deleteFn">删除</el-button>
            <el-button icon="yx-bin" @click="startWorkFlow">发起流程</el-button>
        </el-button-group>
    </div>
    <el-table-x ref="reftable" :row-index="true" :checkbox="true" :base-params="baseParams"
                :data-url="urls.index" :table-columns="tableColumns"></el-table-x>
    <el-dialog-x :title="viewTitle[viewType]" :visible.sync="dialogVisible" width="650px">
        <el-form-x ref="reform"
                   :group-fields="updateFields" :buttons="updateButtons"
                   :disabled="formDisabled" label-width="80px"></el-form-x>
    </el-dialog-x>
    <yufp-wf-init ref="yufpWfInit" @afterinit="onAfterInit" @afterclose="onAfterClose" :common-params="wfCommonParams" />
</div>