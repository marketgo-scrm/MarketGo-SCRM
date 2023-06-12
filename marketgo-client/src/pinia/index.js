import { defineStore } from 'pinia'

export const wecomPinia = defineStore('wecom', {
    state: () => ({
        _detail: {
        },
        _list: [],
        _curTodo:{},
        _user:null,
        _app:{
            corp_id:'',
            agent_id:''
        }
    }),
    getters: {
        detail: (state) => state._detail,
        list: (state) => state._list,
        user: (state) => state._user,
        app: (state) => state._app,
    },
    actions: {
        setDetail(data) {
            this._detail = data||{}
        },
        setToUser(info){
            this._curTodo.user=info
        },
        setUser(user){
            this._user=user
        },
        setApp(config){
        this._app=config
        }
    },
    persist: true
})