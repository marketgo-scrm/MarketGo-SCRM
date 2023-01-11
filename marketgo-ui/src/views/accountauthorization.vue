<template>
  <div class="content">
    <div class="top">
      <div class="logon">
        <img src="../assets/imgs/Union.png" alt="" />
        MarketGo
      </div>
      <div class="navbar">
        <div class="nav">
          <div class="listone project">
            {{ this.$store.state.project[0]?.projectName }}
            <i class='el-icon-caret-bottom'> </i>
            <div class='menus'>
              <div class='menulist' @click='gohome' v-for='(item, index) in $store.state.project' :key='index'>
                {{ item?.projectName }}
              </div>
            </div>
          </div>
          <div class="listone" v-for="(item, index) in list" :key="index" @click="jumpUrl(item)">
            {{ item.name }}
          </div>
        </div>
        <div class="vips" @click="jumpUrl({ name: '升级' })">
          <span>
            升级至VIP尊享版
          </span>
          <img src="../assets/imgs/Group.png" alt="" />
        </div>
      </div>
    </div>
    <div class='contout'>
      <div class="tops">
        <div class='iconout' @click="backs">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="title">
          <h3>账号授权</h3>
          <!-- <span>xxxxxx</span> -->
        </div>
      </div>
      <div class="outs">
        <div class="step">
          <el-scrollbar style="height: 100%">
            <div class="stepin">
              <h3 class="titles">接入企业微信服务步骤</h3>
              <el-timeline>
                <el-timeline-item v-for="(item, index) in activities" :key="index" :type="item.type">
                  <template slot="dot">
                    <div class="circle">
                      {{ index + 1 }}
                    </div>
                  </template>
                  <div class="tmecont">
                    <h3 class="timetiyle">
                      {{ item.content }}
                    </h3>
                    <div class="listout">
                      <div class="lists" @click="gostep(val)" v-for="val in item.children" :key="val.type">
                        <div :class="{
                          finish: nowtype > val.type,
                          nowlod: nowtype == val.type,
                        }">
                          {{ val.name }}
                        </div>
                      </div>
                    </div>
                  </div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-scrollbar>
        </div>
        <div class="seting">
          <el-scrollbar ref="scroll" style="height: 100%">
            <div class="stepin">
              <h3 class="settitle">{{ chosedata.name }}</h3>
              <div class="texts">
                <p v-for="(text, index) in chosedata.text" :key="index">
                  {{ text }}
                </p>
              </div>
              <div v-show="chosedata.dicts">
                <div class="dictlist" v-for="(item, index) in chosedata.dicts" :key="index + 'a'">
                  <span>{{ index + 1 }}
                    <span v-html='item.dist'></span>
                  </span>
                  <img v-for="(val, idx) in item.imglist" :key="idx + 'b'" :src="val" alt="" />
                </div>
              </div>
              <el-form label-position="left" v-show="nowtype == 0" :model="corp" size="small" ref="corp"
                :disabled="isdisabled" :rules="rules" label-width="80px">
                <el-form-item label="企业名称" prop="corpName">
                  <el-input v-model="corp.corpName" placeholder="请输入企业名称" clearable />
                </el-form-item>
                <el-form-item label="企业ID" prop="corpId">
                  <el-input v-model="corp.corpId" placeholder="请输入企业ID" clearable />
                </el-form-item>
              </el-form>
              <el-form label-position="left" v-show="nowtype == 2" :model="agent" size="small" ref="agent"
                :rules="rules" label-width="80px">
                <el-form-item label="应用ID" prop="agentId">
                  <el-input v-model="agent.agentId" placeholder="请输入应用ID" clearable />
                </el-form-item>
                <el-form-item label="Secret" prop="secret">
                  <el-input v-model="agent.secret" placeholder="请输入Secret" clearable />
                </el-form-item>
              </el-form>
              <el-form label-position="left" v-show="nowtype == 4 || nowtype == 5" :model="contacts" size="small"
                ref="contacts" :rules="contactsrules" label-width="110px">
                <template v-if="nowtype == 4">
                  <el-form-item label="通讯录Secret" prop="secret">
                    <el-input v-model="contacts.secret" placeholder="请输入通讯录Secret" clearable />
                  </el-form-item>
                </template>
                <template v-else>
                  <el-form-item label="URL" prop="url">
                    <!-- <el-row>
                    <el-col :span="10"> -->
                    <el-input v-model="contacts.url" disabled placeholder="请输入URL" clearable />
                    <!-- </el-col>
                    <el-col :span="3" :offset="1"> -->
                    <el-button class='copys' type="primary" round @click="copytext(contacts.url)">复制</el-button>
                    <!-- </el-col>
                  </el-row> -->
                  </el-form-item>
                  <el-form-item label="Token" prop="token">
                    <!-- <el-row>
                    <el-col :span="10"> -->
                    <el-input disabled v-model="contacts.token" placeholder="请输入Token" clearable />
                    <!-- </el-col>
                    <el-col :span="3" :offset="1"> -->
                    <el-button type="primary" round class='copys' @click="copytext(contacts.token)">复制</el-button>
                    <!-- </el-col>
                  </el-row> -->
                  </el-form-item>
                  <el-form-item label="EncodingAESKey" prop="encodingAesKey">
                    <!-- <el-row>
                    <el-col :span="10"> -->
                    <el-input disabled v-model="contacts.encodingAesKey" placeholder="请输入EncodingAESKey" clearable />
                    <!-- </el-col>
                    <el-col :span="3" :offset="1"> -->
                    <el-button type="primary" round class='copys'
                      @click="copytext(contacts.encodingAesKey)">复制</el-button>
                    <!-- </el-col>
                  </el-row> -->
                  </el-form-item>
                </template>
              </el-form>
              <!-- externalUser -->
              <el-form label-position="left" v-show="nowtype == 6 || nowtype == 7 || nowtype == 8" :model="externalUser"
                size="small" ref="externalUser" :rules="externalUserrules" label-width="110px">
                <template v-if="nowtype == 6">
                  <el-form-item label="客户联系Secret" prop="secret">
                    <el-input v-model="externalUser.secret" placeholder="客户联系Secret" clearable />
                  </el-form-item>
                </template>
                <template v-else-if="nowtype == 7">
                  <el-form-item label="URL" prop="url">
                    <!-- <el-row>
                    <el-col :span="10"> -->
                    <el-input disabled v-model="externalUser.url" placeholder="请输入URL" clearable />
                    <!-- </el-col>
                    <el-col :span="3" :offset="1"> -->
                    <el-button type="primary" round class='copys' @click="copytext(externalUser.url)">复制</el-button>
                    <!-- </el-col>
                  </el-row> -->
                  </el-form-item>
                  <el-form-item label="Token" prop="token">
                    <!-- <el-row>
                    <el-col :span="10"> -->
                    <el-input disabled v-model="externalUser.token" placeholder="请输入Token" clearable />
                    <!-- </el-col>
                    <el-col :span="3" :offset="1"> -->
                    <el-button type="primary" round class='copys' @click="copytext(externalUser.token)">复制</el-button>
                    <!-- </el-col>
                  </el-row> -->
                  </el-form-item>
                  <el-form-item label="EncodingAESKey" prop="encodingAesKey">
                    <!-- <el-row>
                    <el-col :span="10"> -->
                    <el-input disabled v-model="externalUser.encodingAesKey" placeholder="请输入EncodingAESKey"
                      clearable />
                    <!-- </el-col>
                    <el-col :span="3" :offset="1"> -->
                    <el-button type="primary" round class='copys'
                      @click="copytext(externalUser.encodingAesKey)">复制</el-button>
                    <!-- </el-col>
                  </el-row> -->
                  </el-form-item>
                </template>
              </el-form>
              <el-form label-position="left" v-show="nowtype == 9" :model="domain" size="small" ref="domain"
                label-width="110px">
                <template v-if="nowtype == 9">
                  <div v-for="(item, index) in this.domainInfo" :key="index">
                    <p class="callback_tip"> 可作为应用OAuth2.0网页授权功能的回调域名：</p>
                    <el-form-item label="可用域名" prop="secret">
                      <el-input v-model="item.domainUrl" disabled placeholder="" clearable />
                      <el-button type="primary" round class='copys' @click="copytext(item.domainUrl)">复制</el-button>
                    </el-form-item>
                  </div>
                  <p class="callback_tip"> 请上传下载的验证归属文件【.txt】</p>
                  <el-form-item label="上传文件">
                    <el-upload ref="upfile" :action="uploadUrl" style="display: inline" :auto-upload="true"
                      :show-file-list="false" :file-list="fileList" :on-change="handleChange"
                      :on-progress="uploadVideoProcess" :headers="{
                        'header-api-token': token
                      }">
                      <el-button style="margin-right:10px">选择本地文件</el-button>
                    </el-upload>
                    <span style="display: inline">
                      <el-progress v-if="progressFlag" :percentage="loadProgress" color="#92E780"></el-progress>
                      
                      <em v-if="!progressFlag && fileName.length">
                        <img style="width:12px;height:10px; margin-left: 10px" src="../assets/file_icon.png" alt="">
                        {{ fileName }}
                      </em>
                      <span class="close" v-if="fileName.length > 0 || progressFlag" @click="deleteFile">
                        <i class="el-icon-close"></i>
                      </span>
                    </span>

                  </el-form-item>

                </template>

              </el-form>

              <div class="btns">
                <el-button size="small" round @click="prive" v-if="nowtype != 0">返回上一步</el-button>
                <el-button round type="primary" size="small" @click="next" :loading="loading">{{
                  chosedata.formkey
                    ? "保存并进入下一步"
                    : nowtype != 9
                      ? "下一步"
                      : "完成"
                }}</el-button>
              </div>
            </div>
          </el-scrollbar>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import constants from '@/constants/constants.js'
export default {
  data() {
    
    return {
      list: [
        // { name: "平台运营项目", path: "" },
        { name: "更新日志", path: "" },
        { name: "帮助文档", path: "" },
        { name: "价格", path: "" },
      ],
      token: localStorage.getItem('token'),
      uploadUrl: `${this.$global.BASEURL}/mktgo/wecom/corp/cred/upload?corp_id=${this.$store.state.corpId}&project_id=${this.$store.state.projectUuid}`,
      activities: [
        {
          content: "配置企业为微信账号",
          children: [
            {
              name: "输入企业微信ID和企业名称",
              formkey: "corp",
              type: 0,
              text: [
                "配置企业微信ID和企业微信名称是使用本系统的必须条件，不配置本系统所有功能都无法正常使用。",
                "企业微信ID为你注册企业微信的时候企业微信官方为每个账号分配的唯一标示“CorpID”，企业名称为你的企业微信简称。",
                "将该两项填入下方输入框，即完成企业微信账号的接入。",
              ],
              dicts: [
                {
                  dist: "获取企业名称和企业ID，请用管理员账号扫码登录 <a target='_blank' class='acls' href='https://work.weixin.qq.com/wework_admin/loginpage_wx?from=myhome'>企业微信管理后台</a>",
                  imglist: [require("../assets/step/1.png")],
                },
              ],
            },
          ],
        },
        {
          content: "配置自建应用",
          children: [
            {
              name: "创建自建应用",
              type: 1,
              text: [
                "配置自建应用后可以使用以下功能。",
                "批量加好友：通过自建应用接收加好友的任务。",
              ],
              dicts: [
                {
                  dist: "登录成功后，在应用管理【自建应用模块】点击【创建应用】",
                  imglist: [require("../assets/step1/1.png")],
                },
                {
                  dist: "进入创建应用页面，上传【应用logo】和填写【应用名称】，并且一定选择应用可见范围+",
                  imglist: [require("../assets/step1/2.png")],
                },
                {
                  dist: "点击创建应用后，返回【应用管理】，就可以找到你刚才创建的应用了。",
                  imglist: [require("../assets/step1/3.png")],
                },
                {
                  dist: "点击应用图标进入详情，将服务器的外网IP配置到【企业可信IP】中",
                  imglist: [require("../assets/step1/4.png")],
                },
              ],
            },
            {
              name: "输入自建应用ID和Secret",
              type: 2,
              formkey: "agent",
              configType: "AGENT",
              text: [
                "输入自建应用的ID和Secret后，系统才能调用自建应用的能力，同时API接口将调用失败。所以这一步一定要仔细配置，避免配置错，影响系统使用。",
              ],
              dicts: [
                {
                  dist: "在应用管理【自建应用模块】，找到刚刚创建的【自建应用】，点击进入应用详情页",
                  imglist: [require("../assets/step2/1.png")],
                },
                {
                  dist: "进入应用详情页面，将AgentId和Secret分别填入下方输入框，Secret点击查看后到企业微信上查看",
                  imglist: [
                    require("../assets/step2/2-1.png"),
                    require("../assets/step2/2-2.png"),
                    require("../assets/step2/2-3.png"),
                    require("../assets/step2/2-4.png"),
                  ],
                },
                {
                  dist: "确认应用状态为【已启用】，否则API接口将调用失败，同时应用对所有员工不可见",
                  imglist: [require("../assets/step2/3.png")],
                },
              ],
            },
            {
              name: "配置可调用的应用",
              type: 3,
              text: [
                "配置可调用的应用是指业自建应用可以调用可见范围内的外部联系人相关接口，实现更多丰富的交互功能。",
              ],
              dicts: [
                {
                  dist: "在【客户联系】页面上方点击【API】按钮，展开配置模块",
                  imglist: [require("../assets/step3/1.png")],
                },
                {
                  dist: "找到你刚才创建的【应用】，在后面选择，要确保已经选择应用，点击确定保存",
                  imglist: [require("../assets/step3/2.png")],
                },
                {
                  dist: "点击创建应用后，返回【应用管理】，就可以找到你刚才创建的应用了。",
                  imglist: [require("../assets/step3/3.png")],
                },
              ],
            },
          ],
        },
        {
          content: "配置企业通讯录",
          children: [
            {
              name: "输入通讯录Secret",
              type: 4,
              formkey: "contacts",
              configType: "CONTACTS",
              text: [
                "通讯录Secret配置好后，系统才能同步通讯录成员及组织架构等数据，所以无论如何也要保证配置正确，否则会影响数据的同步和系统的使用。",
              ],
              dicts: [
                {
                  dist: "从在管理工具页面，点击【通讯录同步】",
                  imglist: [require("../assets/step4/1.png")],
                },
                {
                  dist: "按一下步骤，获取通讯录【Secret】对应信息，填入下方输入框",
                  imglist: [
                    require("../assets/step4/2-1.png"),
                    require("../assets/step4/2-2.png"),
                    require("../assets/step4/2-3.png"),
                  ],
                },
              ],
            },
            {
              name: "设置通讯录接收事件服务器",
              type: 5,
              configType: "CONTACTS",
              formkey: "contacts",
              text: [
                "通讯录Secret配置好后，系统才能同步通讯录成员及组织架构等数据，所以无论如何也要保证配置正确，否则会影响数据的同步和系统的使用。",
              ],
              dicts: [
                {
                  dist: "从在管理工具页面，点击【通讯录同步】",
                  imglist: [require("../assets/step5/1.png")],
                },
                {
                  dist: "获取编辑接收事件服务器的权限",
                  imglist: [
                    require("../assets/step5/2-1.png"),
                    require("../assets/step5/2-2.png"),
                    require("../assets/step5/2-3.png"),
                    require("../assets/step5/2-4.png"),
                  ],
                },
                {
                  dist: "将下面信息填到对应的输入框，然后点保存即可",
                  imglist: [require("../assets/step5/3.png")],
                },
              ],
            },
          ],
        },
        {
          content: "配置客户联系",
          children: [
            {
              name: "配置客户联系Secret",
              type: 6,
              formkey: "externalUser",
              configType: "EXTERNAL_USER",
              text: [
                "客户联系Secret配置好后，系统才能同步客户和客户群等相关数据和调用客户联系的API能力，所以无论如何也要保证配置正确，否则会影响数据的同步和系统的使用。",
              ],
              dicts: [
                {
                  dist: "在【客户联系】页面上方点击【API】按钮，展开配置模块",
                  imglist: [require("../assets/step6/1.png")],
                },
                {
                  dist: "按以下步骤，获取客户联系【Secret】对应信息，填入下方输入框",
                  imglist: [
                    require("../assets/step6/2-1.png"),
                    require("../assets/step6/2-2.png"),
                    require("../assets/step6/2-3.png"),
                    require("../assets/step6/2-4.png"),
                  ],
                },
              ],
            },
            {
              name: "设置客户联系接收事件服务器",
              type: 7,
              formkey: "externalUser",
              configType: "EXTERNAL_USER",
              text: [
                "客户联系Secret配置好后，系统才能同步客户和客户群等相关数据和调用客户联系的API能力，所以无论如何也要保证配置正确，否则会影响数据的同步和系统的使用。",
              ],
              dicts: [
                {
                  dist: "在【客户联系】页面上方点击【API】按钮，展开配置模块",
                  imglist: [require("../assets/step7/1.png")],
                },
                {
                  dist: "获取编辑接收事件服务器的权限",
                  imglist: [
                    require("../assets/step7/2.png")
                  ],
                },
                {
                  dist: "将下面信息填到对应的输入框，然后点保存即可",
                  imglist: [require("../assets/step7/3.png")],
                },
              ],
            },
            {
              name: "设置客户联系使用范围",
              type: 8,
              text: [
                "只有配置的客户联系使用权限范围的员工才能通外部客户沟通，并使用系统的的能力。",
              ],
              dicts: [
                {
                  dist: "在【管理工具】下点击【企业可信IP】配置",
                  imglist: [
                    require("../assets/step8/1-1.png"),
                    require("../assets/step8/1-2.png"),
                  ],
                },
                {
                  dist: "在【我的企业】页面，点击左侧【外部沟通管理】，设置客户联系使用范围，点击确认，建议给所有人都有客户联系权限，避免一些因权限产生的意外问题",
                  imglist: [require("../assets/step8/2-1.png"), require("../assets/step8/2-2.png")],
                }
              ],
            },
          ],
        },
        {
          content: "配置聊天侧边栏",
          children: [
            {
              name: "配置网页授权JS-SDK",
              type: 9,
              // formkey: "domain",
              configType: "DAMAIN",
              text: [
                "企业微信聊天侧边栏需使用微信JS-SDK、跳转小程序等, 需要完成可信域名验证和域名归属验证。否则无法正常使用侧边栏。"
              ],
              dicts: [
                {
                  dist: "在【应用管理/应用】下点击 MarketGo",
                  imglist: [
                    require("../assets/step9/1-1.png"),
                  ],
                },
                {
                  dist: "在【在网页授权及JS-SDK】下点击【设置可信域名】",
                  imglist: [
                    require("../assets/step9/1-2.png"),
                  ],
                 
                },
                {
                  dist: "请复制一下域名信息填写到上图中的相应位置，并选中【用于OAuth2回调的可信域名是否验证】，下载上图中的文件，然后通过下面的接口上传。",
                  imglist: [
                    require("../assets/step9/1-3.png"),
                  ],
                 
                },
              ],
            },
            // {
            //   name: "配置客户画像和群画像",
            //   type: 10,
            //   text: [
            //     "开通成功后，登录企业微信，在与客户的聊天对话框右侧点击侧边栏的图标，点击开始使用后即可使用"
            //   ],

            // },
            // {
            //   name: "配置素材库",
            //   type: 11,
            //   text: [
            //     "帮助企业高效的与客户沟通，支持添加文字、图片、外链、图文链接、第三方链接等多类型消息，可对消息进行分类方便管理与查找，同时支持一键发送，帮助企业员工提高工作效率",
            //   ],
            // },
          ],
        },
      ],
      chosedata: {},
      corp: {
        // corpId: "wwa67b5f2bf5754641",
        // corpName: "河北婉峥科技有限公司",
      },
      agent: {
        // agentId: "1000002",
        // secret: "IA6a9_onHKt6Tn6E3L3_GR-clQa6Vmy2kXYS_t1lZ6c",
      },
      contacts: {
        // secret: "ikTaFT0It7DqCu0NJzIo66xLKivRzXIX3ar2TD7vT48",
      },
      externalUser: {
        // secret: "dXCpsNbuLrcio5qGcwkgvDwELaLbCv0wANmaDYGbuYw",
      },
      domainInfo: [],

      rules: {
        corpName: [
          { required: true, message: "请输入企业名称", trigger: "blur" },
        ],
        corpId: [{ required: true, message: "请输入企业ID", trigger: "blur" }],
        secret: [{ required: true, message: "请输入Secret", trigger: "blur" }],
        agentId: [{ required: true, message: "请输入应用ID", trigger: "blur" }],
      },
      contactsrules: {
        secret: [{ required: true, message: "请输入Secret", trigger: "blur" }],
        url: [{ required: true, message: "请输入UEL", trigger: "blur" }],
        token: [{ required: true, message: "请输入Token", trigger: "blur" }],
        encodingAesKey: [
          { required: true, message: "请输入EncodingAESKey", trigger: "blur" },
        ],
      },
      externalUserrules: {
        secret: [{ required: true, message: "请输入Secret", trigger: "blur" }],
        url: [{ required: true, message: "请输入UEL", trigger: "blur" }],
        token: [{ required: true, message: "请输入Token", trigger: "blur" }],
        encodingAesKey: [
          { required: true, message: "请输入EncodingAESKey", trigger: "blur" },
        ],
      },
      nowtype: 0,
      lasttype: 0,
      formlist: [],
      project_id: "",
      loading: false,
      isdisabled: false,
      fileName: '',
      loadProgress: 0, // 动态显示进度条
      progressFlag: false, // 关闭进度条

    };
  },
  mounted() {
    this.project_id = this.$store.state.projectUuid

    if (this.$route.params.configure) {
      this.isdisabled = true
      let configure = this.$route.params.configure
      let idx = -1
      for (let k in configure) {
        if (configure[k]) {
          this[k] = configure[k]
          //alert(JSON.stringify(this[k]))
          idx++
        }
      }

      if (idx == this.activities.length - 1) {
        this.lasttype = this.activities[idx].children[this.activities[idx].children.length - 1].type
      } else {
        this.lasttype = this.activities[idx + 1].children[0].type
      }
      this.nowtype = 0
    }
    this.getdata();
    // 获取企微的可信域名
    this.gethttps('domain/query');
  },
  methods: {
    gohome() {
      this.$router.push({
        name: 'home'
      })
    },
    copytext(val) {
      var cInput = document.createElement("input");
      cInput.value = val;
      document.body.appendChild(cInput);
      cInput.select(); // 选取文本框内容
      document.execCommand("copy");
      this.$message({
        type: "success",
        message: "复制成功",
      });
      document.body.removeChild(cInput);
    },
    backs() {
      this.$router.go(-1);
    },
    prive() {
      this.nowtype--;
      this.getdata();
    },
    handleChange(file, fileList) {
      this.fileName = file.name
      this.fileList = fileList
    },
    jumpUrl(item) {
      if (item.name === '更新日志') {
        window.open(constants.UPDATE_LOG_URL, "_blank")
      }
      else if (item.name === '帮助文档') {
        window.open(constants.HELP_URL, "_blank")
      }
      else if (item.name === '价格') {
        window.open(constants.PRICE_URL, "_blank")
      }
      else if (item.name === '升级') {
        window.open(constants.PRICE_URL, "_blank")
      }
    },

    //  上传进度
    uploadVideoProcess(event) {
      this.progressFlag = true; // 显示进度条
      this.loadProgress = parseInt(event.percent); // 动态获取文件上传进度
      if (this.loadProgress >= 100) {
        this.loadProgress = 100
        setTimeout(() => { this.progressFlag = false }, 500) // 一秒后关闭进度条
      }
    },
    // 删除文件
    deleteFile() {
      this.$confirm('是否确定删除？', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(() => {

        if (this.progressFlag) {
          //取消上传
          this.$refs.upfile.abort();
          this.fileName = ''
          this.fileList = []
          this.loadProgress = 0// 动态显示进度条
          this.progressFlag = false
        }
        else {
          // 网络删除
          this.deleteFilePost()
        }


      }).catch(() => {

      })
    },
    async deleteFilePost() {
      let data = await this.$http.post(
            `/mktgo/wecom/corp/cred_file/delete?project_id=${this.project_id}&corp_id=${this.$store.state.corpId}&file_name=${this.fileName}`
          );
          if (data.code === 0) {
            this.$message.success('删除成功')
            this.fileName = ''
            this.fileList = []
            this.loadProgress = 0// 动态显示进度条
            this.progressFlag = false
          }
    },
    next() {
      if (this.nowtype == 9) {
        this.$message.success("保存成功");
        this.$router.push({
          path: '/index'
        })
        return false;
      }
      if (!this.chosedata.formkey) {
        this.loading = true;
        this.nowtype++;
        if (this.lasttype < this.nowtype) {
          this.lasttype++;
        }
        this.getdata();
        this.loading = false;
        return false;
      }
      this.$refs[this.chosedata.formkey].validate(async (valid) => {
        if (valid) {
          this.loading = true;
          let params = {
            corp: this.corp,
          };
          if (this.nowtype == 0) {
            this.isdisabled = true;
            this.nowtype++;
            if (this.lasttype < this.nowtype) {
              this.lasttype++;
            }
            this.getdata();
            this.loading = false;
            return false;
          } else {
            params[this.chosedata.formkey] = this[this.chosedata.formkey];
            params.configType = this.chosedata.configType;
          }

          let data = await this.$http.post(
            `mktgo/wecom/corp/save?project_id=${this.project_id}`,
            params
          );
          //alert(JSON.stringify(params))
          if (data.code === 0) {
            this.nowtype++;
            if (this.lasttype < this.nowtype) {
              this.lasttype++;
            }
            if (this.nowtype == 5 || this.nowtype == 7) {
              this.gethttps('config');
            }
            this.getdata();
          }
          this.loading = false;
        } else {
          return false;
        }
      });
    },

    async gethttps(path) {

      let data = await this.$http.get(
        `mktgo/wecom/corp/${path}?project_id=${this.project_id}&corp_id=${this.$store.state.corpId}`
      );

      if (data.code === 0) {
        if (path == 'domain/query') {
          this.domainInfo = [data.data, data.data];
          this.fileName = data.data.credFileName || ''
        }
        else {
          this[this.chosedata.formkey] = {
            ...data.data.configs[0][this.chosedata.formkey],
            ...{ secret: this[this.chosedata.formkey].secret },
          };
        }
      }
    },

    getdata() {
      this.activities.map((item) => {
        // let idx=0
        item.children.map((val) => {

          if (val.type == this.nowtype) {
            this.chosedata = val;
          }
        });
      });
      this.$refs["scroll"].wrap.scrollTop = 0;
    },
    gostep(val) {

      if (this.lasttype < val.type) {
        return false;
      }
      this.nowtype = val.type;
      this.getdata();
    },
  },
};
</script>
<style scoped>
.content {
  width: 100%;
  height: 100vh;
  background: #fff;
  background: #f0f2f6;
}

.contout {
  padding: 20px;
  box-sizing: border-box;
}

.tops {
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.tops i {
  cursor: pointer;
}

.title {
  height: 40px;
  display: flex;
  font-size: 12px;
  margin-left: 20px;
  flex-direction: column;
  justify-content: center;
}

.title h3 {
  font-size: 18px;
}

.outs {
  width: 100%;
  height: calc(100vh - 150px);
  display: flex;
  justify-content: space-between;
}

.step {
  width: 270px;
  height: 100%;
  background: #fff;
  border-radius: 14px;
}

.stepin {
  width: 100%;
  padding: 24px;
  box-sizing: border-box;
}

.step .titles {
  font-size: 13px;
  margin: 6px 0 30px 0;
}

.seting {
  width: calc(100% - 285px);
  height: 100%;
  background: #fff;
}

.circle {
  width: 18px;
  height: 18px;
  background: #4f618c;
  color: #fff;
  text-align: center;
  line-height: 18px;
  border-radius: 9px;
  margin-left: -4px;
}

.tmecont {
  width: 100%;
}

.timetiyle {
  width: 100%;
  line-height: 26px;
  font-size: 12px;
  color: #000;
}

.finish {
  color: #67c23a;
}

.nowlod {
  color: #679BFF;
}

.listout {
  padding-top: 15px;
  box-sizing: border-box;
}

.lists {
  width: 100%;
  height: 30px;
  line-height: 30px;
  font-size: 12px;
  text-indent: 10px;
  cursor: pointer;
  color: #999;
}

.settitle {
  font-size: 15px;
}

.texts {
  width: 720px;
  font-size: 12px;
  background: #fef6e0;
  color: #eaa04a;
  padding: 14px 16px;
  box-sizing: border-box;
  margin: 30px 0;
}

.texts p {
  line-height: 28px;
}

.btns {
  /* text-indent: 140px; */
}

.dictlist {
  width: 720px;
}

.dictlist>span {
  display: inline-block;
  font-weight: 600;
  font-size: 12px;
  margin-bottom: 16px;
}

.dictlist img {
  width: 720px;
  margin-bottom: 16px;
}

::v-deep(.el-scrollbar__view) {
  height: 100%;
}

::v-deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

::v-deep(.el-input) {
  width: 432px;
}

.copys {
  margin-left: 10px;
}

.top {
  width: 100%;
  height: 50px;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.07);
  background: #fff;
}

.logon {
  width: 206px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  float: left;
}

.logon img {
  width: 26px;
  margin-right: 12px;
}

.navbar {
  font-size: 12px;
  width: calc(100% - 206px);
  display: flex;
  height: 50px;
  justify-content: space-between;
  align-items: center;
  padding: 0 28px;
  box-sizing: border-box;
}

.nav {
  width: 400px;
  display: flex;
}

.listone {
  padding: 0 6px;
  height: 50px;
  line-height: 50px;
  margin-left: 20px;
  box-sizing: border-box;
  position: relative;
  cursor: pointer;
}

.vips {
  width: 160px;
  height: 34px;
  border-radius: 17px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: linear-gradient(to right, #fd9d65, #ef65c9);
  font-size: 15px;
  color: #fff;
}

.vips img {
  width: 14px;
  margin-left: 6px;
}

::v-deep(.el-form) {
  background: #F6F6F8;
  padding: 20px 0 2px 20px;
  box-sizing: border-box;
  margin-bottom: 40px;
  width: 720px;
}

::v-deep(.el-input__inner) {
  color: #333 !important;
}

/* /deep/ .el-form-item__label{
  text-indent:24px;
} */
::v-deep(.el-form-item--mini .el-form-item__content, .el-form-item--mini .el-form-item__label) {
  text-indent: 0;
}

.project {
  position: relative;
}

.menus {
  width: 208px;
  padding: 8px 0;
  box-sizing: border-box;
  background: #fff;
  display: none;
  box-shadow: 0px 3px 9px rgba(0, 0, 0, 0.26);
  border-radius: 4px;
  position: absolute;
  top: 40px;
  left: 0;
  z-index: 55;
}

.project:hover .menus {
  display: block;
}

.menulist {
  width: 208px;
  height: 30px;
  text-indent: 10px;
  background: #EFF3FD;
  font-size: 12px;
  line-height: 30px;
  color: #333333;
  cursor: pointer;
}

.callback_tip {
  font-size: 12px;
  line-height: 16.8px;
  color: #333333;
  margin-bottom: 18px;
}

.close {
  /* display: inline-block; */
  margin-left: 10px;
  height: 50%;
  cursor: pointer;
  color: #999999;
  padding: 0 6px;
}

.el-progress {
  display: inline-block;
  width: 52%;
  /* background-color:#000; */
  margin-right: -20px;
}
</style>
<style>
.acls {
  color: #679BFF;
  text-decoration: none
}
</style>