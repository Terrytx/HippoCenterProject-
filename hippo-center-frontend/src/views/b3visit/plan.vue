<template>
<div id="theForm">
    <fieldset>
    <legend>計畫行程</legend>
    <input type="checkbox" v-model="toggleAll" /> 全選/取消全選
    <hr />
    <p>
        <label>
        <input type="checkbox" v-model="selectedItems" value="展覽節目 A" /> 展覽節目 A
        </label>
        <label>
        <input type="checkbox" v-model="selectedItems" value="展覽節目 B" /> 展覽節目 B
        </label>
        <label>
        <input type="checkbox" v-model="selectedItems" value="展覽節目 C" /> 展覽節目 C
        </label>
        <label>
        <input type="checkbox" v-model="selectedItems" value="導覽行程 X" /> 導覽行程 X
        </label>
        <label>
        <input type="checkbox" v-model="selectedItems" value="導覽行程 Y" /> 導覽行程 Y
        </label>
    </p>
    <div id="result">
        已選擇項目: {{ selectedItems.join(", ") || "無" }}
    </div>
    </fieldset>

    <button @click="generatePDF">下載 PDF</button>
</div>
</template>

<script>
import { ref, watch } from "vue";
/*import { jsPDF } from "jspdf";*/

export default {
name: "Plan",
setup() {
    const allItems = ["展覽節目 A", "展覽節目 B", "展覽節目 C", "導覽行程 X", "導覽行程 Y"];
    const selectedItems = ref([]);
    const toggleAll = ref(false);

    // 監聽 toggleAll 變化
    watch(toggleAll, (value) => {
    selectedItems.value = value ? [...allItems] : [];
    });

    // 監聽 selectedItems 變化，更新 toggleAll
    watch(selectedItems, (value) => {
    toggleAll.value = value.length === allItems.length;
    });

    // 產生 PDF
    const generatePDF = () => {
    const doc = new jsPDF();
    doc.setFont("helvetica", "bold");
    doc.text("您的行程安排", 20, 20);
    doc.setFont("helvetica", "normal");

    if (selectedItems.value.length > 0) {
        selectedItems.value.forEach((item, index) => {
        doc.text(`${index + 1}. ${item}`, 20, 40 + index * 10);
        });
    } else {
        doc.text("您尚未選擇任何行程", 20, 40);
    }

    doc.save("計畫行程.pdf");
    };

    return {
    selectedItems,
    toggleAll,
    generatePDF,
    };
},
};
</script>

<style scoped>
button {
margin-top: 10px;
padding: 8px 16px;
background-color: #007bff;
color: white;
border: none;
border-radius: 4px;
cursor: pointer;
}
button:hover {
background-color: #0056b3;
}
</style>
