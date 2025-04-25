# 🏗 Kanban API - Backend Java + Frontend Vue.js + MySQL

Este projeto implementa um sistema Kanban utilizando **Java (NetBeans)** no backend, **Vue.js** no frontend e um **banco de dados MySQL** para armazenar as tarefas.

## 📌 Tecnologias utilizadas
- **Backend**: Java (NetBeans), MySQL Connector/J (`mysql-connector-j-8.0.33.jar`)
- **Frontend**: Vue.js
- **Banco de Dados**: MySQL

## 📂 Configuração do Banco de Dados
Antes de rodar a aplicação, crie o banco de dados e suas tabelas no **MySQL**.

### 🔹 Criação do banco e tabela
Execute o seguinte **script SQL** no seu MySQL:

```sql
CREATE DATABASE kanban;

USE kanban;

CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    status ENUM('A Fazer', 'Em Progresso', 'Concluído') NOT NULL
);

🛠 Passos para rodar o Backend (Java)
1️⃣ Baixe e instale o MySQL

Se ainda não tiver o MySQL instalado, siga as instruções oficiais: MySQL Downloads

2️⃣ Baixe o mysql-connector-j-8.0.33.jar

Link oficial: MySQL Connector/J

3️⃣ Adicione o JAR ao NetBeans

No NetBeans, vá até Libraries e clique em Add JAR/Folder.

Selecione mysql-connector-j-8.0.33.jar.

4️⃣ Compile e execute o servidor

Rode os comandos abaixo no terminal dentro do NetBeans:
mvn clean install
java -cp target/kanbanapi-1.0-SNAPSHOT.jar:lib/mysql-connector-j-8.0.33.jar com.mycompany.kanbanapi.KanbanServer
Isso iniciará o servidor na porta 8000.

🌐 Passos para rodar o Frontend (Vue.js)
1️⃣ Instale o Node.js e Vue CLI

Baixe e instale Node.js

Execute:

bash
npm install -g @vue/cli
2️⃣ Clone o repositório do frontend

bash
git clone https://github.com/seuprojeto/kanban-frontend.git
cd kanban-frontend
npm install
3️⃣ Inicie o frontend

bash
npm run dev
O projeto estará rodando em http://localhost:5173/.

🐞 Bug Atual
Atualmente, há um problema onde tarefas enviadas para a coluna "Concluído" não estão sendo exibidas corretamente no frontend. A função que filtra as tarefas pode estar retornando um array vazio ou o status pode não estar sendo corretamente atualizado no banco. 🚨