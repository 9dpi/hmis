document.addEventListener('DOMContentLoaded', () => {
    const taskInput = document.getElementById('taskInput');
    const addTaskBtn = document.getElementById('addTaskBtn');
    const taskList = document.getElementById('taskList');

    // Hàm render hoặc thêm task
    const addTask = () => {
        const taskText = taskInput.value.trim();
        if (taskText === '') {
            alert('Vui lòng nhập nội dung task!');
            return;
        }

        // 1. Tạo các phần tử DOM
        const listItem = document.createElement('li');
        listItem.className = 'task-item';
        
        const taskSpan = document.createElement('span');
        taskSpan.className = 'task-text';
        taskSpan.textContent = taskText;

        // 2. Tạo nút hoàn thành
        const completeBtn = document.createElement('button');
        completeBtn.textContent = 'Hoàn thành';
        completeBtn.className = 'complete-btn';
        completeBtn.onclick = () => toggleComplete(listItem, completeBtn);

        // 3. Tạo nút xóa
        const deleteBtn = document.createElement('button');
        deleteBtn.textContent = 'Xóa';
        deleteBtn.className = 'delete-btn';
        deleteBtn.onclick = () => deleteTask(listItem);

        // 4. Gắn các phần tử vào listItem
        listItem.appendChild(taskSpan);
        const actionsDiv = document.createElement('div');
        actionsDiv.className = 'task-actions';
        actionsDiv.appendChild(completeBtn);
        actionsDiv.appendChild(deleteBtn);
        listItem.appendChild(actionsDiv);

        // 5. Thêm task mới vào list
        taskList.appendChild(listItem);

        // 6. Xóa nội dung input và focus lại
        taskInput.value = '';
        taskInput.focus();
    };

    // Xử lý sự kiện nhấn nút "Thêm Task"
    addTaskBtn.addEventListener('click', addTask);

    // Xử lý sự kiện nhấn Enter trong input
    taskInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addTask();
        }
    });

    // Chức năng chuyển trạng thái hoàn thành (Toggle Complete)
    const toggleComplete = (listItem, btn) => {
        listItem.classList.toggle('completed');
        if (listItem.classList.contains('completed')) {
            btn.textContent = 'Hoàn tác';
            btn.style.backgroundColor = '#ffc107'; // Màu vàng hơn khi hoàn tác
        } else {
            btn.textContent = 'Hoàn thành';
            btn.style.backgroundColor = '#28a745';
        }
    };

    // Chức năng xóa task
    const deleteTask = (listItem) => {
        if (confirm('Bạn có chắc chắn muốn xóa task này?')) {
            listItem.remove();
        }
    };
});