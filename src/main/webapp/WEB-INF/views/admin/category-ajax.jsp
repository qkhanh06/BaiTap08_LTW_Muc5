<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true"%>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Category AJAX CRUD</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script>const contextPath = "<%= request.getContextPath() %>";</script>
</head>
<body>
<nav class="navbar navbar-dark bg-dark mb-4"><div class="container">
    <a class="navbar-brand" href="<%= request.getContextPath() %>/admin/categories">Spring Boot AJAX CRUD</a>
    <div><a class="btn btn-outline-light me-2" href="<%= request.getContextPath() %>/admin/products">Products</a>
    <a class="btn btn-warning" target="_blank" href="<%= request.getContextPath() %>/swagger-ui.html">Swagger</a></div>
</div></nav>
<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-3"><h2>Category</h2>
        <button class="btn btn-success" onclick="openAddModal()">+ Thêm Category</button></div>
    <div class="input-group mb-3">
        <input id="keyword" class="form-control" placeholder="Tìm theo tên Category...">
        <button id="btnSearch" class="btn btn-primary">Tìm kiếm</button>
    </div>
    <div id="alertBox"></div>
    <div class="table-responsive"><table class="table table-striped table-bordered align-middle">
        <thead class="table-dark"><tr><th>ID</th><th>Icon</th><th>Name</th><th style="width:180px">Actions</th></tr></thead>
        <tbody id="categoryBody"></tbody>
    </table></div>
    <nav><ul id="pagination" class="pagination"></ul></nav>
</div>

<div class="modal fade" id="categoryModal" tabindex="-1"><div class="modal-dialog"><div class="modal-content">
<form id="categoryForm" enctype="multipart/form-data">
    <div class="modal-header"><h5 class="modal-title" id="modalTitle">Category</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
    <div class="modal-body">
        <input type="hidden" id="categoryId" name="categoryId">
        <div class="mb-3"><label class="form-label">Category Name</label><input class="form-control" id="categoryName" name="categoryName" required></div>
        <div class="mb-3"><label class="form-label">Icon</label><input type="file" class="form-control" id="icon" name="icon" accept="image/*"></div>
    </div>
    <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button><button class="btn btn-primary" type="submit">Lưu</button></div>
</form>
</div></div></div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
let currentPage = 0;
let modal;
$(function(){
    modal = new bootstrap.Modal(document.getElementById('categoryModal'));
    loadCategories(0);
    $('#btnSearch').on('click', () => loadCategories(0));
    $('#keyword').on('keyup', e => { if(e.key === 'Enter') loadCategories(0); });
    $('#categoryForm').on('submit', saveCategory);
});
function showAlert(message, type='success') { $('#alertBox').empty().append($('<div>').addClass('alert alert-' + type).text(message)); }
function loadCategories(page){
    $.get(contextPath + '/api/category/search', {keyword: $('#keyword').val() || '', page, size: 5})
      .done(data => {
        if (page > 0 && data.totalPages > 0 && page >= data.totalPages) {
            loadCategories(data.totalPages - 1);
            return;
        }
        currentPage = data.number;
        let html = '';
        data.content.forEach(c => {
            const image = c.icon ? `<img src="${contextPath}/uploads/${c.icon}" style="width:64px;height:64px;object-fit:cover">` : '-';
            html += `<tr><td>${c.categoryId}</td><td>${image}</td><td>${escapeHtml(c.categoryName)}</td><td>
                <button class="btn btn-sm btn-warning me-1" onclick="openEditModal(${c.categoryId})">Sửa</button>
                <button class="btn btn-sm btn-danger" onclick="deleteCategory(${c.categoryId})">Xóa</button></td></tr>`;
        });
        $('#categoryBody').html(html || '<tr><td colspan="4" class="text-center">Không có dữ liệu</td></tr>');
        renderPagination(data.number, data.totalPages);
      }).fail(xhr => showAlert(xhr.responseText || 'Không tải được dữ liệu', 'danger'));
}
function renderPagination(page, totalPages){
    let html = '';
    for(let i=0;i<totalPages;i++) html += `<li class="page-item ${i===page?'active':''}"><a class="page-link" href="#" onclick="loadCategories(${i});return false;">${i+1}</a></li>`;
    $('#pagination').html(html);
}
function openAddModal(){
    $('#modalTitle').text('Thêm Category'); $('#categoryForm')[0].reset(); $('#categoryId').val(''); $('#icon').prop('required', false); modal.show();
}
function openEditModal(id){
    $.post(contextPath + '/api/category/getCategory', {id}).done(res => {
        const c = res.body;
        $('#modalTitle').text('Cập nhật Category'); $('#categoryForm')[0].reset(); $('#categoryId').val(c.categoryId); $('#categoryName').val(c.categoryName); modal.show();
    }).fail(xhr => showAlert(readError(xhr), 'danger'));
}
function saveCategory(e){
    e.preventDefault();
    const id = $('#categoryId').val();
    const formData = new FormData(this);
    if(id && !$('#icon')[0].files.length) formData.delete('icon');
    $.ajax({
        url: contextPath + (id ? '/api/category/updateCategory' : '/api/category/addCategory'),
        type: id ? 'PUT' : 'POST', data: formData, contentType:false, processData:false
    }).done(res => { modal.hide(); showAlert(res.message || 'Thành công'); loadCategories(id ? currentPage : 0); })
      .fail(xhr => showAlert(readError(xhr), 'danger'));
}
function deleteCategory(id){
    if(!confirm('Bạn chắc chắn muốn xóa Category này?')) return;
    $.ajax({url: contextPath + '/api/category/deleteCategory?categoryId=' + id, type:'DELETE'})
      .done(res => { showAlert(res.message || 'Đã xóa'); loadCategories(currentPage); })
      .fail(xhr => showAlert(readError(xhr), 'danger'));
}
function readError(xhr){ try { return JSON.parse(xhr.responseText).message || xhr.responseText; } catch(e){ return xhr.responseText || 'Có lỗi xảy ra'; } }
function escapeHtml(text){ return $('<div>').text(text || '').html(); }
</script>
</body></html>
