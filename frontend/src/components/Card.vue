<template>
    <div class="card">
        <h3>{{ task.titulo }}</h3>
        <p>{{ task.descricao }}</p>
        <div class="actions">
            <button @click="$emit('edit', task)">✏️</button>
            <button @click="$emit('remove', task.id)">🗑️</button>
        </div>
    </div>
</template>

<script setup lang="ts">
defineProps({
    task: {
        type: Object,
        required: true
    }
})
defineEmits(['edit', 'remove'])
</script>

<style scoped>
.card {
    background-color: #f5f5f5;
    border-radius: 8px;
    padding: 1rem;
    margin-bottom: 1rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.actions {
    display: flex;
    justify-content: space-between;
    margin-top: 0.5rem;
}

button {
    cursor: pointer;
    border: none;
    background: none;
    font-size: 1.2rem;
}
</style>