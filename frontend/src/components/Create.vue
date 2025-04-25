<template>
  <div class="form">
    <input v-model="form.titulo" placeholder="Título" />
    <textarea v-model="form.descricao" placeholder="Descrição"></textarea>
    <select v-model="form.status">
      <option>A Fazer</option>
      <option>Em Progresso</option>
      <option>Concluido</option>
    </select>
    <div class="actions">
      <button @click="create">Criar</button>
      <button @click="$emit('cancel')">Cancelar</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps({
  task: {
    type: Object,
    required: true
  }
})
const emit = defineEmits(['create', 'cancel'])

const form = ref({ ...props.task })

watch(() => props.task, (novaTask) => {
  form.value = { ...novaTask }
})

const create = async () => {
  await fetch('http://localhost:8000/tasks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(form.value)
  })

  emit('create')
  form.value = { ...props.task }
}
</script>

<style scoped>
.form {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  background-color: #eeeeee;
  padding: 1rem;
  border-radius: 8px;
}

.actions {
  display: flex;
  justify-content: space-between;
}

button {
  padding: 0.5rem 1rem;
  cursor: pointer;
}
</style>