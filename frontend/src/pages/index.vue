<template>
    <div class="kanban-container">
        <button @click="mostrarCriar = !mostrarCriar">
            {{ mostrarCriar ? 'Fechar Formulário ✖️' : 'Nova Tarefa ➕' }}
        </button>

        <Create v-if="mostrarCriar" :task="{ titulo: '', descricao: '', status: 'A Fazer' }" @create="carregarTasks"
            @cancelar="mostrarCriar = false" />

        <div class="kanban-board">
            <div v-for="coluna in colunas" :key="coluna" class="kanban-column" @dragover.prevent
                @drop="soltarTarefa(coluna)">
                <h2>{{ coluna }}</h2>
                <div class="tasks">
                    <Card v-for="task in tasksPorStatus(coluna)" :key="task.id" :task="task" draggable="true"
                        @dragstart="arrastarTarefa(task)" @edit="editarTask" @remove="removerTask" />
                </div>
            </div>
        </div>
        <div v-if="taskEmEdicao" class="form-edicao">
            <input v-model="taskEmEdicao.titulo" placeholder="Título" />
            <input v-model="taskEmEdicao.descricao" placeholder="Descrição" />
            <select v-model="taskEmEdicao.status">
                <option>A Fazer</option>
                <option>Em Progresso</option>
                <option>Concluído</option>
            </select>
            <button @click="salvarEdicao">Salvar</button>
            <button @click="cancelarEdicao">Cancelar</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Card from '../components/Card.vue'
import Create from '../components/Create.vue'

interface Task {
    id: number
    titulo: string
    descricao: string
    status: string
}

const tasks = ref<Task[]>([])
const mostrarCriar = ref(false)
const tarefaArrastada = ref<Task | null>(null)
const colunas = ['A Fazer', 'Em Progresso', 'Concluido']

const carregarTasks = async () => {
    const res = await fetch('http://localhost:8000/tasks')
    tasks.value = await res.json()
}

const editarTask = (task: Task) => {
    taskEmEdicao.value = { ...task };
};

const taskEmEdicao = ref<Task | null>(null)
    const salvarEdicao = async () => {
    if (taskEmEdicao.value) {
        await editarTask(taskEmEdicao.value);
        
        const index = tasks.value.findIndex(t => t.id === taskEmEdicao.value?.id);
        if (index !== -1) {
            tasks.value[index] = { ...taskEmEdicao.value };
        }      
        taskEmEdicao.value = null;
    }
};

const cancelarEdicao = () => {
    taskEmEdicao.value = null
}


const removerTask = async (id: number) => {
    await fetch(`http://localhost:8000/tasks/${id}`, {
        method: 'DELETE'
    })
    tasks.value = tasks.value.filter(t => t.id !== id)
}

const arrastarTarefa = (task: Task) => {
    tarefaArrastada.value = task
}

const soltarTarefa = async (novoStatus: string) => {
    if (tarefaArrastada.value && tarefaArrastada.value.status !== novoStatus) {
        tarefaArrastada.value.status = novoStatus
        await editarTask(tarefaArrastada.value)
        tarefaArrastada.value = null
    }
}

const tasksPorStatus = (status: string) => {
    return tasks.value.filter(task => task.status === status)
}

onMounted(carregarTasks)
</script>

<style scoped>
.kanban-container {
    padding: 2rem;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.kanban-board {
    display: flex;
    gap: 1rem;
    margin-top: 2rem;
    width: 100%;
    justify-content: center;
}

.kanban-column {
    background-color: #f0f0f0;
    border-radius: 8px;
    padding: 1rem;
    width: 300px;
    min-height: 300px;
}

.tasks {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

h2 {
    text-align: center;
}

button {
    background-color: #42b983;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 0.5rem 1rem;
    cursor: pointer;
}
</style>