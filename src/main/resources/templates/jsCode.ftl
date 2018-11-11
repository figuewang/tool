define([

], function (require, exports) {
    exports.ready = function (hashCode, data, cite) {
        yufp.custom.vue({
            el: cite.el,
            data: function () {
                var _self = this;
                return {                   
                    urls:{
                        index:backend.example+ '/api/${table.domainname}/',
                        show:backend.example+ '/api/${table.domainname}/',
                        create:backend.example+ '/api/${table.domainname}/',
                        update:backend.example+ '/api/${table.domainname}/update/',
                        batchdelete:backend.example+ '/api/${table.domainname}/batchdelete/'
                    },
                    tableColumns: [
<#list table.cols as a>
						{ label: '${a.name}', prop: '${a.prop}', resizable: true },
</#list> 
                    ],
                    updateFields: [
                        {
                            columnCount: 2,
                            fields: [
<#list table.cols as a>                           
								{label: '${a.name}',field: '${a.prop}', type: '${a.type}' ,rules: [{ required: ${a.required}, message: '必填项'}]},
</#list> 
                            ]
                        }
                    ],
                    queryFields: [
<#list table.cols as a>
						{ placeholder: '${a.name}', field: '${a.prop}', type: '${a.type}' },
</#list> 
                    ],
                    baseParams: {
                        condition: {
                        }
                    },
                    queryButtons: [
                        {
                            label: '搜索', op: 'submit', type: 'primary', icon: 'search', click: function (model, valid) {
                                if (valid) {
                                    var param = { condition: JSON.stringify(model) };
                                    _self.$refs.reftable.remoteData(param);
                                }
                            }
                        },
                        { label: '重置', op: 'reset', type: 'primary', icon: 'yx-loop2' }
                    ],
                    updateButtons: [
                        {
                            label: '取消', type: 'primary', icon: 'yx-undo2', hidden: false, click: function (model) {
                                _self.dialogVisible = false;
                            }
                        },
                        {
                            label: '保存', type: 'primary', icon: 'check', hidden: false, click: function (model) {
                                var validate = false;
                                _self.$refs.reform.validate(function (valid) {
                                    validate = valid;
                                });
                                if (!validate) {
                                    return;
                                }
                                var formUrl = _self.urls.create;
                                if(_self.viewType == 'EDIT'){
                                    formUrl = _self.urls.update;
                                }
                                yufp.service.request({
                                    method: 'POST',
                                    url: formUrl,
                                    data: model,
                                    callback: function (code, message, response) {
                                        if (code == 0) {
                                            _self.$refs.reftable.remoteData();
                                            _self.$message('操作成功');
                                            _self.dialogVisible = false;
                                        }
                                    }
                                });
                            }
                        }
                    ],
                    height: yufp.frame.size().height - 103,
                    dialogVisible: false,
                    formDisabled: false,
                    viewType: 'DETAIL',
                    viewTitle: yufp.lookup.find('CRUD_TYPE', false)
                }
            },
            methods: {
                /**
                * @param viewType 表单类型
                * @param editable 可编辑,默认false
                */
                switchStatus: function (viewType, editable) {
                    var _self = this;
                    _self.viewType = viewType;
                    _self.updateButtons[1].hidden = !editable;
                    _self.formDisabled = !editable;
                    _self.dialogVisible = true;
                },
                addFn: function () {
                    var _self = this;
                    _self.switchStatus('ADD', true);
                    _self.$nextTick(function () {
                        _self.$refs.reform.resetFields();
                    });
                },
                modifyFn: function () {
                    if (this.$refs.reftable.selections.length != 1) {
                        this.$message({ message: '请先选择一条记录', type: 'warning' });
                        return;
                    }
                    this.switchStatus('EDIT', true);
                    this.$nextTick(function () {
                        var obj = this.$refs.reftable.selections[0];
                        yufp.extend(this.$refs.reform.formModel, obj);
                    });
                },
                infoFn: function () {
                    if (this.$refs.reftable.selections.length != 1) {
                        this.$message({ message: '请先选择一条记录', type: 'warning' });
                        return;
                    }
                    this.switchStatus('DETAIL', false);
                    this.$nextTick(function () {
                        yufp.extend(this.$refs.reform.formModel, this.$refs.reftable.selections[0]);
                    });
                },
                deleteFn: function () {
                    var _self = this;
                    var selections = _self.$refs.reftable.selections;
                    if (selections.length < 1) {
                        _self.$message({ message: '请先选择一条记录', type: 'warning' });
                        return;
                    }
                    var len = selections.length, arr = [];
                    for (var i = 0; i < len; i++) {
                        arr.push(selections[i].${table.pkColName});
                    }
                    yufp.service.request({
                        method: 'POST',
                        url: _self.urls.batchdelete+arr.join(','),
                        callback: function (code, message, response) {
                            if (code == 0) {
                                _self.$refs.reftable.remoteData();
                                _self.$message('操作成功');
                            }
                        }
                    });
                }
            }
        });
    };
});