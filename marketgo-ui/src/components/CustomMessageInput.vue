<template>
  <div class="custom-message">
    <div
      class="custom-message-input"
      contenteditable="true"
      ref="messageRef"
      :style="{ minHeight: rows * 24 + 'px' }"
      readonly
      @blur="blur"
      @input="input"
      @dragstart="dragStart($event)"
    ></div>
    <div class="custom-message-number">{{ val.length }}/{{ limit }}</div>
    <div class="custom-message-operate">
      <span
        >插入：
        <el-popover
          v-model="iconVisible"
          placement="top"
          width="400"
          trigger="click"
        >
          <div class="custom-message-operate-box">
            <el-button
              size="medium"
              type="text"
              v-for="(icon, key) in emoji_icon"
              :key="key"
              style="margin: 0 10px 0 0"
              @click="keepLastIndex(icon)"
            >
              {{ icon }}
            </el-button>
          </div>
          <el-button slot="reference" size="small" type="text">😊</el-button>
        </el-popover>
      </span>
      <el-divider direction="vertical" v-show="nameBtnShow"></el-divider>
      <el-button v-show="nameBtnShow" @click="keepLastIndex('#{EXTERNAL_USER_NAME}#')" size="small" type="text">客户昵称</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "CustomMessageInput",
  emits: ["change"],
  props: {
    // index
    index: Number,
    // 值
    value: {
      type: String,
      default: "",
    },
    // 最大值
    rows: {
      type: Number,
      default: 4,
    },
    // 最小值
    min: {
      type: Number,
      default: 999,
    },
    // 框型号
    size: {
      type: String,
      default: "",
    },
    limit: {
      type: Number,
      default: 800,
    },
    nameBtnShow: {
      type: Boolean,
      default: true
    }
    //
    // placeholder: {
    //   type: String,
    //   default: "",
    // },
  },
  data() {
    return {
      iconVisible: false,
      lastEditRange: 0,
      emoji_icon: [
        "😀",
        "😃",
        "😄",
        "😁",
        "😆",
        "😅",
        "🤣",
        "😂",
        "🙂",
        "🙃",
        "😉",
        "😊",
        "😇",
        "😍",
        "🤩",
        "😘",
        "😗",
        "😚",
        "😙",
        "😋",
        "😛",
        "😜",
        "🤪",
        "😝",
        "🤑",
        "🤗",
        "🤭",
        "🤫",
        "🤔",
        "🤐",
        "🤨",
        "😐",
        "😑",
        "😶",
        "😏",
        "😒",
        "🙄",
        "😬",
        "🤥",
        "😌",
        "😔",
        "😪",
        "🤤",
        "😴",
        "😷",
        "🤒",
        "🤕",
        "🤢",
        "🤮",
        "🤧",
        "😵",
        "🤯",
        "🤠",
        "😎",
        "🤓",
        "🧐",
        "😕",
        "😟",
        "🙁",
        "😮",
        "😯",
        "😲",
        "😳",
        "😦",
        "😧",
        "😨",
        "😰",
        "😥",
        "😢",
        "😭",
        "😱",
        "😖",
        "😣",
        "😞",
        "😓",
        "😩",
        "😫",
        "😤",
        "😡",
        "😠",
        "🤬",
        "😈",
        "👿",
        "💀",
        "💩",
        "🤡",
        "👹",
        "👺",
        "👻",
        "👽",
        "👾",
        "🤖",
        "😺",
        "😸",
        "😹",
        "😻",
        "😼",
        "😽",
        "🙀",
        "😿",
        "😾",
        "💋",
        "👋",
        "🤚",
        "🖐",
        "✋",
        "🖖",
        "👌",
        "🤞",
        "🤟",
        "🤘",
        "🤙",
        "👈",
        "👉",
        "👆",
        "🖕",
        "👇",
        "👍",
        "👎",
        "✊",
        "👊",
        "🤛",
        "🤜",
        "👏",
        "🙌",
        "👐",
        "🤲",
        "🤝",
        "🙏",
        "💅",
        "🤳",
        "💪",
        "👂",
        "👃",
        "🧠",
        "👀",
        "👁",
        "👅",
        "👄",
      ],
      val: "",
      onindex: this.value.length -1,
    };
  },
  watch: {
    value(data) {
      this.$refs.messageRef.innerHTML = data;
      this.val = data;
    },
  },
  mounted() {
    this.$refs.messageRef.innerHTML = this.value;
    this.val = this.value;
  },
  methods: {
    getPosition(element) {
      console.log(9999999999999,element.ownerDocument)
      var caretOffset = 0;
      var doc = element.ownerDocument || element.document;
      var win = doc.defaultView || doc.parentWindow;
      var sel;
      if (typeof win.getSelection != "undefined") {
        //谷歌、火狐
        sel = win.getSelection();
        if (sel.rangeCount > 0) {
          var range = sel.getRangeAt(0);
          var preCaretRange = range.cloneRange(); //克隆一个选区
          preCaretRange.selectNodeContents(element); //设置选区的节点内容为当前节点
          preCaretRange.setEnd(range.endContainer, range.endOffset); //重置选中区域的结束位置
          caretOffset = preCaretRange.toString().length;
        }
      } else if ((sel = doc.selection) && sel.type != "Control") {
        //IE
        var textRange = sel.createRange();
        var preCaretTextRange = doc.body.createTextRange();
        preCaretTextRange.moveToElementText(element);
        preCaretTextRange.setEndPoint("EndToEnd", textRange);
        caretOffset = preCaretTextRange.text.length;
      }
      return caretOffset;
    },
    insertStr(source, start, newStr) {
      return source.slice(0, start) + newStr + source.slice(start)
    },
    keepLastIndex(icon) {
      if (this.$refs.messageRef.innerHTML.length < this.limit) {
        let text = this.insertStr(this.$refs.messageRef.innerHTML, this.onindex, icon)
        this.val = text;
        this.synchro(text);
      } else {
        this.$refs.messageRef.innerHTML = this.val;
      }
      setTimeout(() => {
        const obj = this.$refs.messageRef;
        console.log(window.getSelection);

        console.log(document.selection);

        if (window.getSelection) {
          //ie11 10 9 ff safari

          obj.focus(); //解决ff不获取焦点无法定位问题

          var range = window.getSelection(); //创建range

          range.selectAllChildren(obj); //range 选择obj下所有子内容
          console.log(999, obj.childNodes, range);
          range.collapseToEnd(); //光标移至最后
        } else if (document.selection) {
          // eslint-disable-next-line no-redeclare
          var range = document.selection.createRange(); //创建选择对象
          //var range = document.body.createTextRange();
          range.moveToElementText(obj); //range定位到obj
          range.collapse(false); //光标移至最后
          range.select();
        }
        this.iconVisible = false;
      }, 50);
    },
    synchro(text) {
      this.$emit("change", text, this.index);
    },
    input(e) {
      if (e.target.innerHTML.length <= this.limit) {
        this.val = e.target.innerHTML;
      } else {
        this.$refs.messageRef.innerHTML = this.val;
        this.$refs.messageRef.blur();
      }
      console.log(this.val);
    },
    blur(e) {
      console.log(e, this.$refs,this.getPosition(this.$refs.messageRef));
      this.onindex = this.getPosition(this.$refs.messageRef)
      let str = e.target.outerText.trim()
      this.synchro(str);
      this.$refs.messageRef.innerHTML = str;
      this.val = str;
    },
    //开始拖动可选字段
    dragStart(event) {
      event = event || window.event;
      console.log(5, event);
    },
  },
};
</script>
<style lang="scss" scoped>
.custom-message {
  mix-blend-mode: normal;
  border-radius: 2px;
  border: 1px solid #e0e0e0;
  box-sizing: border-box;
  background: #ffffff;
  font-family: PingFang SC;
  .custom-message-input {
    padding: 8px 9px;
    line-height: 24px;
    white-space: pre-wrap;
    &[contenteditable]:focus {
      outline: none;
    }
  }
  .custom-message-number {
    color: #999999;
    font-size: 12px;
    padding-right: 9px;
    text-align: right;
    margin-top: -10px;
  }
  .custom-message-operate {
    opacity: 0.8;
    border-radius: 2px;
    background: #f5f5f5;
    padding: 4px 9px;
    .custom-message-operate-box {
      overflow-y: auto;
      max-height: 300px;
    }
  }
}
</style>