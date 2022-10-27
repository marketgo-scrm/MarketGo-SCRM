<template>
  <div class="Enclosure">
    <div class="style-tab">
      <div class="style-item">
        <el-radio v-model="radioFj" label="1">图片</el-radio>
      </div>
      <div class="style-item">
        <el-radio v-model="radioFj" label="2">网页</el-radio>
      </div>
      <div class="style-item" v-show="!isPyq">
        <el-radio v-model="radioFj" label="3">小程序</el-radio>
      </div>
    </div>
    <div class="content">
      <!--图片 朋友圈     -->
      <div v-show="radioFj == 1 && isPyq" class="pyqBox" style="padding-top: 11px;margin-left: 11px">
        <el-upload
            :action="post"
            list-type="picture-card"
            :multiple="false"
            :limit="9"
            :file-list="typeImg.fileList"
            :on-change="typeImgchange"
            :on-success="typeImgSec"
            :auto-upload="true"
            :headers="{
              'header-api-token': token
            }"
            :class="typeImg.iconHide  ? 'imgUpBox-icon-noshow' : ''"
        >
          <i slot="default" class="el-icon-plus"></i>
          <div slot="file" slot-scope="{file}">
            <img
                class="el-upload-list__item-thumbnail"
                :src="typeImg.dataInImg ? typeImg.dataInImg : file.url" alt=""
            >
            <span class="el-upload-list__item-actions">
              <span
                  class="el-upload-list__item-preview"
                  @click="typeImghandlePictureCardPreview(file)"
              >
                <i class="el-icon-zoom-in"></i>
              </span>
                    <!--        <span
                                v-if="!typeImg.disabled"
                                class="el-upload-list__item-delete"
                                @click="typeImghandleDownload(file)"
                            >
                              <i class="el-icon-download"></i>
                            </span>-->
              <span
                  v-if="!typeImg.disabled"
                  class="el-upload-list__item-delete"
                  @click="typeImghandleRemove(file)"
              >
                <i class="el-icon-delete"></i>
              </span>
            </span>
          </div>
        </el-upload>
        <div class="tip">图片10MB内，不超过1440 x 1080 PX，最多可添加9张图片</div>
        <el-dialog :visible.sync="typeImg.dialogVisible">
          <img width="100%" :src="typeImg.dialogImageUrl" alt="">
        </el-dialog>
      </div>
      <!--图片      -->
<!--      :file-list="typeImg.fileList"-->
      <div v-show="radioFj == 1 && !isPyq" class="tp" style="padding-top: 11px;margin-left: 11px">
        <el-upload
            :action="post"
            list-type="picture-card"
            :multiple="false"
            :limit="1"
            :file-list="typeImg.fileList"
            :on-change="typeImgchange"
            :on-success="typeImgSec"
            :auto-upload="true"
            :headers="{
              'header-api-token': token
            }"
            :class="typeImg.iconHide  ? 'imgUpBox-icon-noshow' : ''"
        >
          <i slot="default" class="el-icon-plus"></i>
          <div slot="file" slot-scope="{file}">
            <img
                class="el-upload-list__item-thumbnail"
                :src="typeImg.dataInImg ? typeImg.dataInImg : file.url" alt=""
            >
            <span class="el-upload-list__item-actions">
        <span
            class="el-upload-list__item-preview"
            @click="typeImghandlePictureCardPreview(file)"
        >
          <i class="el-icon-zoom-in"></i>
        </span>
<!--        <span
            v-if="!typeImg.disabled"
            class="el-upload-list__item-delete"
            @click="typeImghandleDownload(file)"
        >
          <i class="el-icon-download"></i>
        </span>-->
        <span
            v-if="!typeImg.disabled"
            class="el-upload-list__item-delete"
            @click="typeImghandleRemove(file)"
        >
          <i class="el-icon-delete"></i>
        </span>
      </span>
          </div>
        </el-upload>
        <div class="tip">注：图片最大限制10MB内</div>
        <el-dialog :visible.sync="typeImg.dialogVisible">
          <img width="100%" :src="typeImg.dialogImageUrl" alt="">
        </el-dialog>
      </div>
      <!--网页      -->
      <div v-show="radioFj == 2" class="wy" style="padding-top: 21px">
        <el-form ref="baseForm" class="demo-baseForm" label-width="80px">
          <el-form-item label="活动信息" required>
            <el-input v-model="typeLink.title"
                      maxlength="30"
                      show-word-limit
                      style="width:calc(100% - 10px);margin-bottom: 20px"
                      placeholder="请填写标题"
            ></el-input>
          </el-form-item>
          <el-form-item label="摘 要">
            <el-input v-model="typeLink.des"
                      type="textarea"
                      :rows="5"
                      maxlength="100"
                      show-word-limit
                      style="width:calc(100% - 10px);margin-bottom: 20px"
                      placeholder="请填写内容"
            ></el-input>
          </el-form-item>
          <el-form-item label="网 址" required>
            <el-input v-model="typeLink.url"
                      maxlength="30"
                      show-word-limit
                      style="width:calc(100% - 10px);margin-bottom: 20px"
                      placeholder="请填写URL"
            ></el-input>
          </el-form-item>
          <div class="tooltip">
            <p style="margin-top: 6px">请填写以 HTTP或HTTPS 为开头的链接（例如</p>
            <p>https://www.abc.com）</p>
            <span class="triangle"></span>
          </div>
          <el-form-item label="链接封面">
            <el-upload
                :action="post1"
                list-type="picture-card"
                :multiple="false"
                :limit="1"
                :file-list="typeLink.fileList"
                :on-change="typeLinkchange"
                :on-success="typeLinkSec"
                :auto-upload="true"
                :headers="{
                  'header-api-token': token
                }"
                :class="typeLink.iconHide  ? 'imgUpBox-icon-noshow' : ''"
            >
              <i slot="default" class="el-icon-plus"></i>
              <div slot="file" slot-scope="{file}">
                <img
                    class="el-upload-list__item-thumbnail"
                    :src="typeLink.dataInImg ? typeLink.dataInImg : file.url" alt=""
                >
                <span class="el-upload-list__item-actions">
                  <span
                      class="el-upload-list__item-preview"
                      @click="typeLinkhandlePictureCardPreview(file)"
                  >
                    <i class="el-icon-zoom-in"></i>
                  </span>
<!--                  <span
                      v-if="!typeLink.disabled"
                      class="el-upload-list__item-delete"
                      @click="typeLinkhandleDownload(file)"
                  >
                    <i class="el-icon-download"></i>
                  </span>-->
                  <span
                      v-if="!typeLink.disabled"
                      class="el-upload-list__item-delete"
                      @click="typeLinkhandleRemove(file)"
                  >
                    <i class="el-icon-delete"></i>
                  </span>
                </span>
              </div>
            </el-upload>
            <div class="tip">注：{{!isPyq ? '图片最大限制2MB内，建议尺寸400 x 400 PX' : '图片最大限制10MB内，尺寸不超过1440 x 1080 PX'}}</div>
            <el-dialog :visible.sync="typeLink.dialogVisible">
              <img width="100%" :src="typeLink.dialogImageUrl" alt="">
            </el-dialog>
          </el-form-item>

        </el-form>
      </div>
      <!--小程序      -->
      <div v-show="radioFj == 3" class="xcx" style="padding-top: 21px">
        <el-form ref="baseForm" class="demo-baseForm" label-width="105px">
          <el-form-item label="小程序标题息" required>
            <el-input v-model="miniApp.title"
                      maxlength="30"
                      show-word-limit
                      style="width:calc(100% - 10px);margin-bottom: 20px"
                      placeholder="请填写标题"
            ></el-input>
          </el-form-item>
          <el-form-item label="小程序APPID" required>
            <el-input v-model="miniApp.appid"
                      maxlength="30"
                      show-word-limit
                      style="width:calc(100% - 10px);margin-bottom: 20px"
                      placeholder="请填写"
            ></el-input>
          </el-form-item>
          <el-form-item label="小程序路径" required>
            <el-input v-model="miniApp.path"
                      maxlength="30"
                      show-word-limit
                      style="width:calc(100% - 10px);margin-bottom: 20px"
                      placeholder="请填写"
            ></el-input>
          </el-form-item>
          <el-form-item label="小程序封面图" required>
            <el-upload
                :action="post2"
                list-type="picture-card"
                :multiple="false"
                :limit="1"
                :file-list="miniApp.fileList"
                :on-change="miniAppchange"
                :on-success="miniAppSec"
                :auto-upload="true"
                :headers="{
                  'header-api-token': token
                }"
                :class="miniApp.iconHide  ? 'imgUpBox-icon-noshow' : ''"
            >
              <i slot="default" class="el-icon-plus"></i>
              <div slot="file" slot-scope="{file}">
                <img
                    class="el-upload-list__item-thumbnail"
                    :src="miniApp.dataInImg ? miniApp.dataInImg : file.url" alt=""
                >
                <span class="el-upload-list__item-actions">
                  <span
                      class="el-upload-list__item-preview"
                      @click="miniApphandlePictureCardPreview(file)"
                  >
                    <i class="el-icon-zoom-in"></i>
                  </span>
<!--                  <span
                      v-if="!miniApp.disabled"
                      class="el-upload-list__item-delete"
                      @click="miniApphandleDownload(file)"
                  >
                    <i class="el-icon-download"></i>
                  </span>-->
                  <span
                      v-if="!miniApp.disabled"
                      class="el-upload-list__item-delete"
                      @click="miniApphandleRemove(file)"
                  >
                    <i class="el-icon-delete"></i>
                  </span>
                </span>
              </div>
            </el-upload>
            <div class="tip">注：最多2MB，建议尺寸 520*416 PX</div>
            <el-dialog :visible.sync="miniApp.dialogVisible">
              <img width="100%" :src="miniApp.dialogImageUrl" alt="">
            </el-dialog>
          </el-form-item>

        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Enclosure",
  props:{
    index: Number,
    dataIn: Object,
    callback: Function,
    type: String,
    isPyq: Boolean
  },
  data() {
    return {
      token: localStorage.getItem('token'),
      post:`${this.$global.BASEURL}mktgo/wecom/media/upload?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_type=IMAGE&task_type=${this.type}`,
      post1:`${this.$global.BASEURL}mktgo/wecom/media/upload?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_type=LINK&task_type=${this.type}`,
      post2:`${this.$global.BASEURL}mktgo/wecom/media/upload?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_type=MINIPROGRAM&task_type=${this.type}`,
      radioFj: '1',
      typeImg: {
        dialogImageUrl: '',
        dialogVisible: false,
        disabled: false,
        fileList: [],
        objList: [],
        obj:{
          imageContent:'',
          mediaUuid:'',
        },
        dataInImg:'',
        iconHide: false
      },
      typeLink: {
        title: '',
        des: '',
        url: '',
        fileList: [],
        obj:{
          imageContent:'',
          mediaUuid:'',
        },
        dialogImageUrl: '',
        dialogVisible: false,
        disabled: false,
        dataInImg:'',
        iconHide: false
      },
      miniApp: {
        title:'',
        appid:'',
        path:'',
        fileList: [],
        obj:{
          imageContent:'',
          mediaUuid:'',
        },
        dialogImageUrl: '',
        dialogVisible: false,
        disabled: false,
        dataInImg:'',
        iconHide: false
      }
    }
  },
  mounted() {
    if (this.dataIn.typeImg) {
      this.radioFj = '1'
      this.typeImg = this.dataIn.typeImg
    }
    if (this.dataIn.typeLink) {
      console.log(271,this.dataIn.typeLink)
      this.radioFj = '2'
      this.typeLink = this.dataIn.typeLink
    }
    if (this.dataIn.miniApp) {
      this.radioFj = '3'
      this.miniApp = this.dataIn.miniApp
    }
  },
  /*beforeUpdate() {
    if (this.dataIn.typeImg) {
      this.radioFj = '1'
      this.typeImg = this.dataIn.typeImg
    }
    if (this.dataIn.typeLink) {
      this.radioFj = '2'
      this.typeImg = this.dataIn.typeLink
    }
    if (this.dataIn.miniApp) {
      this.radioFj = '3'
      this.typeImg = this.dataIn.miniApp
    }
  },*/
  watch: {
    'radioFj'(n,o){
      console.log(n,o)
      if (n == 1) {
        this.callback({
          typeImg: this.typeImg
        },this.index)
      }
      if (n == 2) {
        this.callback({
          typeLink: this.typeLink
        },this.index)
      }
      if (n == 3) {
        this.callback({
          miniApp: this.miniApp
        },this.index)
      }
    },
    'typeImg': {
      deep: true,
      handler(n) {
        if (this.radioFj == 1) {
          console.log('typeImg')
          this.callback({
            typeImg: n
          },this.index)
        }
      }
    },
    'typeLink': {
      deep: true,
      handler(n) {
        if (this.radioFj == 2) {
          console.log('typeLink')
          this.callback({
            typeLink: n
          },this.index)
        }
      }
    },
    'miniApp': {
      deep: true,
      handler(n) {
        if (this.radioFj == 3) {
          console.log('miniApp')
          this.callback({
            miniApp: n
          },this.index)
        }
      }
    }
  },
  methods: {
    init(obj) {
      console.log(349,obj)
      let _this = this
      if (obj.typeImg) {
        _this.radioFj = '1'
        _this.typeImg = obj.typeImg
      }
      if (obj.typeLink) {
        _this.radioFj = '2'
        _this.typeLink = obj.typeLink
      }
      if (obj.miniApp) {
        _this.radioFj = '3'
        _this.miniApp = obj.miniApp
      }
      /*setTimeout(function () {
        if (obj.typeImg) {
          _this.radioFj = '1'
          _this.typeImg = obj.typeImg
        }
        if (obj.typeLink) {
          _this.radioFj = '2'
          _this.typeLink = obj.typeLink
        }
        if (obj.miniApp) {
          _this.radioFj = '3'
          _this.miniApp = obj.miniApp
        }
      }, 100)*/
    },
    typeImghandleRemove(file) {
      let _this = this
      console.log(file);
      console.log(this.typeImg.fileList)
      if (!this.isPyq) {
        _this.$http.get(`mktgo/wecom/media/delete?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_uuid=${this.typeImg.obj.mediaUuid}`,
            {});
        _this.typeImg.fileList = []
        _this.typeImg.obj = {
          imageContent: null,
          mediaUuid: null,
        }
      } else {
        console.log(473,file.index)
        _this.$http.get(`mktgo/wecom/media/delete?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_uuid=${this.typeImg.objList[file.index].mediaUuid}`,
            {});
        /*_this.typeImg.fileList = */_this.typeImg.fileList.splice(file.index,1)
        console.log(473,_this.typeImg.fileList)
        /*_this.typeImg.objList = */_this.typeImg.objList.splice(file.index,1)
      }

      setTimeout(function () {
        _this.typeImg.iconHide = false
      }, 400)
    },
    typeImghandlePictureCardPreview(file) {
      this.typeImg.dialogImageUrl = file.url;
      this.typeImg.dialogVisible = true;
    },
    typeImghandleDownload(file) {
      console.log(file);
    },
    typeImgchange(/*file*/) {
      /*console.log(file);
      this.typeImg.fileList.push(file)
      this.typeImg.iconHide = true
      console.log(typeof file)
      this.$http.post(`mktgo/wecom/media/upload?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_type=IMAGE&task_type=${this.type}`, {
        file: file
      }).then(
          function (data) {
            console.log(data)
          }
      )*/
      // console.log(this.typeImg.fileList)
    },
    typeImgSec(response,file) {
      // console.log(response)
      if (response.code == 0) {
        let fileObj = file
        let i = this.typeImg.fileList.length
        fileObj['index'] = i
        this.typeImg.fileList.push(fileObj)
        if (this.isPyq) {
          this.typeImg.objList.push(response.data)
        }
        if (this.isPyq && this.typeImg.fileList.length == 9) {
          this.typeImg.iconHide = true
        } else if (!this.isPyq) {
          this.typeImg.iconHide = true
          this.typeImg.obj = response.data
        }
      } else {
        this.$message({
          message: '上传失败',
          type: 'warning'
        });
      }
    },
    typeLinkhandleRemove(file) {
      let _this = this
      console.log(file);
      console.log(this.typeLink.fileList)
      _this.$http.get(`mktgo/wecom/media/delete?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_uuid=${this.typeImg.obj.mediaUuid}`,
          {});
      _this.typeLink.fileList = []
      _this.typeLink.obj = {
        imageContent: null,
        mediaUuid: null,
      }
      setTimeout(function () {
        _this.typeLink.iconHide = false
      }, 400)
    },
    typeLinkhandlePictureCardPreview(file) {
      this.typeLink.dialogImageUrl = file.url;
      this.typeLink.dialogVisible = true;
    },
    typeLinkhandleDownload(file) {
      console.log(file);
    },
    typeLinkchange(/*file*/) {
      /*console.log(file);
      this.typeLink.fileList.push(file)
      this.typeLink.iconHide = true*/
    },
    typeLinkSec(response,file) {
      // console.log(response)
      if (response.code == 0) {
        this.typeLink.fileList.push(file)
        this.typeLink.iconHide = true
        this.typeLink.obj = response.data
      } else {
        this.$message({
          message: '上传失败',
          type: 'warning'
        });
      }
    },
    miniApphandleRemove(file) {
      let _this = this
      console.log(file);
      console.log(this.miniApp.fileList)
      _this.$http.get(`mktgo/wecom/media/delete?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}&media_uuid=${this.typeImg.obj.mediaUuid}`,
          {});
      _this.miniApp.fileList = []
      _this.miniApp.obj = {
        imageContent: null,
        mediaUuid: null,
      }
      setTimeout(function () {
        _this.miniApp.iconHide = false
      }, 400)
    },
    miniApphandlePictureCardPreview(file) {
      this.miniApp.dialogImageUrl = file.url;
      this.miniApp.dialogVisible = true;
    },
    miniApphandleDownload(file) {
      console.log(file);
    },
    miniAppchange(/*file*/) {
      /*console.log(file);
      this.miniApp.fileList.push(file)
      this.miniApp.iconHide = true*/
    },
    miniAppSec(response,file) {
      // console.log(response)
      if (response.code == 0) {
        this.miniApp.fileList.push(file)
        this.miniApp.iconHide = true
        this.miniApp.obj = response.data
      } else {
        this.$message({
          message: '上传失败',
          type: 'warning'
        });
      }
    },
  }
}
</script>

<style scoped lang="scss">
::v-deep(.el-upload--picture-card) {
  width: 64px;
  height: 64px;
  line-height: 73px;
  margin: 0px 0 0 0px;
}

::v-deep(.el-upload-list__item) {
  margin: 0px 0 0 0px;
}

::v-deep(.imgUpBox-icon-noshow) {
  .el-upload--picture-card {
    display: none !important;
  }
}
::v-deep(.pyqBox) {
  width: 210px;
  .el-upload-list--picture-card .el-upload-list__item {
    width: 64px;
    height: 64px;
    margin-right: 4px;
    border-radius: 0px;
    vertical-align: top;
    margin-bottom: 4px;
  }
}
.Enclosure {
  .style-tab {
    width: 100%;
    //max-width: 609px;
    height: 30px;
    border: 1px solid rgba(228, 228, 228, 1);
    border-radius: 2px;

    .style-item {
      width: 68px;
      height: 30px;
      border-right: 1px solid rgba(228, 228, 228, 1);
      display: inline-block;
      padding: 0 10px;

      .el-radio {
        line-height: 30px !important;
        vertical-align: top;
      }
    }
  }

  .tp,.pyqBox {
    .tip {
      &:before {
        content: '*';
        color: #F56C6C;
        margin-right: 4px;
      }
    }
  }

  .content {
    width: 100%;
    //max-width: 609px;
    vertical-align: top;
    min-height: 100px;
    border: 1px solid rgba(228, 228, 228, 1);
    border-top: 0;

    .tip {
      color: rgb(153, 153, 153);
      margin-left: 0px;
      font-size: 12px;
      white-space: nowrap;
    }

    .tooltip {
      position: relative;
      background-color: white;
      width: calc(100% - 80px);
      //max-width: 526px;
      height: 50px;
      margin-left: 70px;
      border-radius: 0.25em;
      //display: flex;
      justify-content: center;
      //align-items: center;
      text-align: left;
      border: 1px solid #E4E4E4;
      margin-bottom: 10px;
      font-family: 'PingFang SC';
      font-style: normal;
      font-weight: 400;
      font-size: 12px;
      line-height: 20px;
      /* or 16px */
      color: #999999;

      p {
        margin-left: 10px;
      }
    }

    .triangle {
      display: block;
      height: 10px;
      width: 10px;
      background-color: inherit;
      border: inherit;
      position: absolute;
      top: -6px;
      //left: calc(50% - 10px);
      left: 40px;
      // ---关键代码 start---
      clip-path: polygon(0% 0%, 100% 100%, 0% 100%);
      transform: rotate(135deg);
      // ---end---
      //border-radius: 0 0 0 2px;
    }

  }
}
</style>