import { defineStore } from 'pinia'

export const welcomPinia = defineStore('welcom', {
    state: () => ({
        _detail: {
        },
        _list: [],
        _curTodo:{}
    }),
    getters: {
        detail: (state) => state._detail,
        list: (state) => state._list,
    },
    actions: {
        setDetail(data) {
            this._detail = data||{}
        },
        setToUser(info){
            this._curTodo.user=info
        }
    },
})