<template>
  <div class="EnclosureList">
    <div v-if="show">
      <div v-for="(v, k) in arr" :key="k" style="position: relative;width: 100%;">
        <div v-show="k != 0" style="width: 100px; height: 15px"></div>
        <enclosure :ref="'enclosure' + k" :index="k" :dataIn="v" :callback="itemCallback" :type="type" :isPyq="isPyq">
        </enclosure>
        <div v-show="arr.length > 1" :style="k != 0 ? 'top: 24px;' : ''" class="btn-del el-icon-circle-close"
          @click="del(k)"></div>
      </div>
    </div>

    <div @click="add()" class="btn-add" v-show="!limit || (limit && arr.length < limit)">添加内容</div>
  </div>
</template>

<script>
import enclosure from "./Enclosure.vue";
export default {
  name: "EnclosureList",
  components: { enclosure },
  props: {
    callback: Function,
    type: String,
    dataIn: Array,
    limit: Number,
    isPyq: Boolean
  },
  created() {
    this.getsync();
  },
  data() {
    return {
      show: true,
      arr: [
        {
          typeImg: {
            /*dialogImageUrl: "",
            dialogVisible: false,
            disabled: false,
            fileList: [],
            iconHide: false,*/
            dialogImageUrl: '',
            dialogVisible: false,
            disabled: false,
            fileList: [],
            objList: [],
            obj: {
              imageContent: '',
              mediaUuid: '',
            },
            dataInImg: '',
            iconHide: false
          },
        },
      ],
    };
  },
  // watch: {
  //   dataIn(data) {
  //     console.log(789, data);
  //     this.getsync();
  //   },
  // },
  methods: {
    getsync() {
    //  alert(isRefresh)
      if (this.dataIn?.length > 0) {
        for (let i = 0; i < this.dataIn.length; i++) {
          if (this.dataIn[i].image) {
            let tmpObj = this.dataIn[i].image;
            tmpObj.thumbnailContent =
              "data:image/Jpeg;base64," + tmpObj.imageContent;
            this.arr[i] = {
              typeImg: {
                dialogImageUrl: tmpObj.thumbnailContent,
                dialogVisible: false,
                disabled: false,
                fileList: [
                  {
                    name: "",
                    url: tmpObj.thumbnailContent,
                  },
                ],
                obj: {
                  imageContent: tmpObj.imageContent,
                  mediaUuid: tmpObj.uuid,
                },
                dataInImg: tmpObj.thumbnailContent,
                iconHide: true,
              },
            };
          } else if (this.dataIn[i].link) {
            let tmpObj = this.dataIn[i].link;
            tmpObj.thumbnailContent =
              "data:image/Jpeg;base64," + tmpObj.imageContent;
            this.arr[i] = {
              typeLink: {
                title: tmpObj.title,
                des: tmpObj.desc,
                url: tmpObj.url,
                fileList: [
                  {
                    name: "",
                    url: tmpObj.thumbnailContent,
                  },
                ],
                obj: {
                  imageContent: tmpObj.imageContent,
                  mediaUuid: tmpObj.uuid,
                },
                dialogImageUrl: tmpObj.thumbnailContent,
                dialogVisible: false,
                disabled: false,
                dataInImg: tmpObj.thumbnailContent,
                iconHide: true,
              },
            };
          } else if (this.dataIn[i].miniProgram) {
            let tmpObj = this.dataIn[i].miniProgram;
            tmpObj.thumbnailContent =
              "data:image/Jpeg;base64," + tmpObj.imageContent;
            this.arr[i] = {
              miniApp: {
                title: tmpObj.title,
                appid: tmpObj.appId,
                path: tmpObj.page,
                fileList: [
                  {
                    name: "",
                    url: tmpObj.thumbnailContent,
                  },
                ],
                obj: {
                  imageContent: tmpObj.imageContent,
                  mediaUuid: tmpObj.uuid,
                },
                dialogImageUrl: tmpObj.thumbnailContent,
                dialogVisible: false,
                disabled: false,
                dataInImg: tmpObj.thumbnailContent,
                iconHide: true,
              },
            };
          }
        }
        // if (isRefresh) {
        //   for (let i = 0; i < this.arr?.length; i++) {
        //     this.$refs["enclosure" + i][0].init(this.arr[i]);
        //   }
        // }
      }

    },
    add() {
      this.arr.push({
        typeImg: {
          dialogImageUrl: "",
          dialogVisible: false,
          disabled: false,
          fileList: [],
          iconHide: false,
        },
      });
    },
    del(k) {
      console.log(k);
      this.arr.splice(k, 1);
      this.arr = JSON.parse(JSON.stringify(this.arr));
      console.log(99999, this.arr);
      for (let i = 0; i < this.arr.length; i++) {
        // console.log(this.$refs['enclosure'+i])
        this.$refs["enclosure" + i][0].init(this.arr[i]);
      }
      /*this.show = false
      let _this = this
      setTimeout(function () {
        _this.show = true
      },100)*/
    },
    itemCallback(v, k) {
      console.log(v, k);
      this.arr[k] = v;
      this.arr = JSON.parse(JSON.stringify(this.arr));
      console.log(this.arr);
      this.getData();
    },
    getData() {
      let content = [
        /*{
          "text": {
            "content": this.setForm.text
          },
          "type": "TEXT"
        }*/
      ];
      for (let i = 0; i < this.arr.length; i++) {
        if (this.arr[i].typeImg && this.arr[i].typeImg.objList.length) {
          for (let o = 0; o < this.arr[i].typeImg.objList.length; o++) {
            content.push({
              image: {
                // thumbnailContent:this.arr[i].typeImg.obj ? this.arr[i].typeImg.obj.imageContent : '',
                imageContent: this.arr[i].typeImg.objList[o].imageContent,
                mediaUuid: this.arr[i].typeImg.objList[o].mediaUuid,
              },
              type: "IMAGE",
            });
          }
        } else if (this.arr[i].typeImg && this.arr[i].typeImg.obj && this.arr[i].typeImg.obj.imageContent) {
          content.push({
            image: {
              // thumbnailContent:this.arr[i].typeImg.obj ? this.arr[i].typeImg.obj.imageContent : '',
              imageContent: this.arr[i].typeImg.obj
                ? this.arr[i].typeImg.obj.imageContent
                : "",
              mediaUuid: this.arr[i].typeImg.obj
                ? this.arr[i].typeImg.obj.mediaUuid
                : "",
            },
            type: "IMAGE",
          });
        } else if (this.arr[i].typeLink && (
          (this.arr[i].typeLink.obj && this.arr[i].typeLink.obj.imageContent) ||
          this.arr[i].typeLink.title ||
          this.arr[i].typeLink.des ||
          this.arr[i].typeLink.url
        )) {
          content.push({
            link: {
              title: this.arr[i].typeLink.title,
              desc: this.arr[i].typeLink.des,
              url: this.arr[i].typeLink.url,
              imageContent: this.arr[i].typeLink.obj
                ? this.arr[i].typeLink.obj.imageContent
                : "",
              mediaUuid: this.arr[i].typeLink.obj
                ? this.arr[i].typeLink.obj.mediaUuid
                : "",
            },
            type: "LINK",
          });
        } else if (this.arr[i].miniApp && (
          (this.arr[i].miniApp.obj && this.arr[i].miniApp.obj.imageContent) ||
          this.arr[i].miniApp.title ||
          this.arr[i].miniApp.appid ||
          this.arr[i].miniApp.path
        )) {
          content.push({
            miniProgram: {
              title: this.arr[i].miniApp.title,
              appId: this.arr[i].miniApp.appid,
              page: this.arr[i].miniApp.path,
              imageContent: this.arr[i].miniApp.obj
                ? this.arr[i].miniApp.obj.imageContent
                : "",
              mediaUuid: this.arr[i].miniApp.obj
                ? this.arr[i].miniApp.obj.mediaUuid
                : "",
            },
            type: "MINIPROGRAM",
          });
        }
      }

      this.callback(content);
    },
  },
};
</script>

<style scoped lang="scss">
.EnclosureList {
  .btn-add {
    //max-width: 605px;
    margin-left: 2px;
    height: 30px;
    mix-blend-mode: normal;
    border-radius: 2px;
    border: 1px solid rgba(228, 228, 228, 1);
    box-sizing: border-box;
    background: rgba(255, 255, 255, 1);
    text-align: center;
    cursor: pointer;
    margin-top: 20px;
    line-height: 30px;
    color: rgba(51, 51, 51, 1);
    font-family: PingFang SC;
    font-size: 12px;
  }

  .btn-del {
    position: absolute;
    top: 9px;
    right: 25px;
    cursor: pointer;
  }
}
</style>