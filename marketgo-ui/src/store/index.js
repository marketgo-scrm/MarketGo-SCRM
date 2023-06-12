import Vue from 'vue'
import Vuex from 'vuex'
import persistedState from 'vuex-persistedstate';
Vue.use(Vuex)
const store = new Vuex.Store({
    state:{
      user:{},
      projectUuid:'',
      projectName:'',
      corpId:'',
      tenantUuid:'',
      project:[]
    },
    mutations:{
      SET_USER: (state, data) => {
        state.user = data
      },
      SET_PROID:(state, data) => {
        state.projectUuid = data
      },
      SET_PRONAME:(state, data) => {
        state.projectName = data
      },
      SET_CORPID:(state, data) => {
        state.corpId = data
      },
      SET_TENANTUUID:(state, data) => {
        state.tenantUuid = data
      },
      SET_PROJECT:(state, data) => {
        state.project = data
      },
    },
    actions:{
        
    },
    getters: {
      parseId (state) {
        return {
          corp_id: state.corpId,
          project_id: state.projectUuid
        };
      }
    },
    plugins: [
      persistedState({
        // 默认存储在localStorage 现改为sessionStorage
        storage: window.localStorage
      })
    ]
  })
  
  export default store