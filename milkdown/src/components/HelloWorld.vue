<template>
  <div ref="editor"></div>
</template>

<script setup lang="ts">
  import { defaultValueCtx, Editor, rootCtx } from "@milkdown/core";
  import { nord } from "@milkdown/theme-nord";
  import { commonmark } from "@milkdown/preset-commonmark";
  import { onMounted, ref } from "vue";

  const props = defineProps({
      msg: { type: String, default: "" },
  });

  const editor = ref();

  onMounted(() => {
    Editor.make()
      .config((ctx) => {
          ctx.set(rootCtx, editor.value);
          ctx.set(defaultValueCtx, props.msg);
      })
      .use(nord)
      .use(commonmark)
      .create();
  });
</script>

<style></style>
