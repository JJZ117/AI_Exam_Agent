let questions = [];
let answers = {};

// AI generation
function generateAI() {
    fetch('/api/questions/batch/ai-generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            topic: document.getElementById("topic").value,
            count: document.getElementById("count").value
        })
    })
        .then(res => res.json())
        .then(res => {
            questions = res.data;
            renderQuestions();
        });
}

// Render questions
function renderQuestions() {
    let div = document.getElementById("questions");
    div.innerHTML = "";

    questions.forEach((q, index) => {
        let html = `<p>${index + 1}. ${q.title}</p>`;

        if (q.type === "CHOICE") {
            q.choices.forEach(c => {
                html += `<input type="radio" name="${q.id}" value="${c.content}" onchange="selectAnswer(${q.id}, this.value)"> ${c.content}<br>`;
            });
        }

        if (q.type === "JUDGE") {
            html += `<input type="radio" name="${q.id}" value="true" onchange="selectAnswer(${q.id}, this.value)">True
                     <input type="radio" name="${q.id}" value="false" onchange="selectAnswer(${q.id}, this.value)">False`;
        }

        if (q.type === "TEXT") {
            html += `<textarea onchange="inputAnswer(${q.id}, this.value)"></textarea>`;
        }

        div.innerHTML += html;
    });
}

// Record answers
function selectAnswer(id, value) {
    answers[id] = value;
}

function inputAnswer(id, value) {
    answers[id] = value;
}

// Submit answers
function submitAnswers() {
    let arr = [];

    for (let key in answers) {
        arr.push({
            questionId: key,
            userAnswer: answers[key]
        });
    }

    fetch('/api/questions/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(arr)
    })
        .then(res => res.json())
        .then(res => {
            alert("Result: " + JSON.stringify(res.data));
        });
}

// Excel upload
function uploadExcel() {
    let file = document.getElementById("file").files[0];

    let formData = new FormData();
    formData.append("file", file);

    fetch('/api/questions/batch/import-excel', {
        method: 'POST',
        body: formData
    })
        .then(res => res.json())
        .then(() => {
            alert("Upload successful");
        });
}
