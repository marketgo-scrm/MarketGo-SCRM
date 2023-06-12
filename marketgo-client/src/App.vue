<template>
  <div id="app">
    <router-view />
  </div>
</template>
<script>

import { wecomPinia } from "./pinia/";
import qs from "qs";
export default {
  created() {
    const query = qs.parse(location.search.substring(1));

    const corp_id = query.corp_id || "wwde098d24db810862";
    const agent_id = query.agent_id || "1000002";

    const pinia = new wecomPinia();
    pinia.setApp({ corp_id, agent_id });

  },
  methods: {
  },
};
</script>
<style>
body {
  font-size: 16px;
  background-color: #f8f8f8;
  -webkit-font-smoothing: antialiased;
}

.van-loading {
  text-align: center;
}
</style>
