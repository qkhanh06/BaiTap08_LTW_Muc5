<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true"%>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Product AJAX CRUD</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script>const contextPath = "<%= request.getContextPath() %>";</script>
</head>
<body>
<nav class="navbar navbar-dark bg-dark mb-4"><div class="container">
    <a class="navbar-brand" href="<%= request.getContextPath() %>/admin/products">Spring Boot AJAX CRUD</a>
    <div><a class="btn btn-outline-light me-2" href="<%= request.getContextPath() %>/admin/categories">Categories</a>
    <a class="btn btn-warning" target="_blank" href="<%= request.getContextPath() %>/swagger-ui.html">Swagger</a></div>
</div></nav>
<div class="container-fluid px-4">
    <div class="d-flex justify-content-between align-items-center mb-3"><h2>Product</h2><button class="btn btn-success" onclick="openAddModal()">+ Thêm Product</button></div>
    <div class="input-group mb-3"><input id="keyword" class="form-control" placeholder="Tìm theo tên Product..."><button id="btnSearch" class="btn btn-primary">Tìm kiếm</button></div>
    <div id="alertBox"></div>
    <div class="table-responsive"><table class="table table-striped table-bordered align-middle">
        <thead class="table-dark"><tr><th>ID</th><th>Ảnh</th><th>Tên</th><th>Category</th><th>SL</th><th>Giá</th><th>Discount</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody id="productBody"></tbody>
    </table></div>
    <nav><ul id="pagination" class="pagination"></ul></nav>
</div>

<div class="modal fade" id="productModal" tabindex="-1"><div class="modal-dialog modal-lg"><div class="modal-content">
<form id="productForm" enctype="multipart/form-data">
    <div class="modal-header"><h5 class="modal-title" id="modalTitle">Product</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
    <div class="modal-body"><input type="hidden" id="productId" name="productId">
      <div class="row g-3">
        <div class="col-md-8"><label class="form-label">Product Name</label><input class="form-control" id="productName" name="productName" required></div>
        <div class="col-md-4"><label class="form-label">Category</label><select class="form-select" id="categoryId" name="categoryId" required></select></div>
        <div class="col-md-4"><label class="form-label">Quantity</label><input type="number" min="0" class="form-control" id="quantity" name="quantity" required></div>
        <div class="col-md-4"><label class="form-label">Unit Price</label><input type="number" min="0" step="0.01" class="form-control" id="unitPrice" name="unitPrice" required></div>
        <div class="col-md-4"><label class="form-label">Discount</label><input type="number" min="0" step="0.01" class="form-control" id="discount" name="discount" value="0" required></div>
        <div class="col-md-4"><label class="form-label">Status</label><select class="form-select" id="status" name="status"><option value="1">1 - Active</option><option value="0">0 - Inactive</option></select></div>
        <div class="col-md-8"><label class="form-label">Image</label><input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*"></div>
        <div class="col-12"><label class="form-label">Description</label><textarea class="form-control" id="description" name="description" rows="3" required></textarea></div>
      </div>
    </div>
    <div class="modal-footer"><button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button><button class="btn btn-primary" type="submit">Lưu</button></div>
</form>
</div></div></div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
let currentPage=0, modal, categories=[];
$(async function(){
    modal = new bootstrap.Modal(document.getElementById('productModal'));
    await loadCategoryOptions(); loadProducts(0);
    $('#btnSearch').on('click',()=>loadProducts(0));
    $('#keyword').on('keyup',e=>{if(e.key==='Enter')loadProducts(0)});
    $('#productForm').on('submit',saveProduct);
});
function showAlert(message,type='success'){ $('#alertBox').empty().append($('<div>').addClass('alert alert-'+type).text(message)); }
function loadCategoryOptions(){ return $.get(contextPath+'/api/category').done(data=>{
    categories=data; $('#categoryId').html(data.map(c=>`<option value="${c.categoryId}">${escapeHtml(c.categoryName)}</option>`).join(''));
}); }
function loadProducts(page){
    $.get(contextPath+'/api/product/search',{keyword:$('#keyword').val()||'',page,size:5}).done(data=>{
        if(page>0 && data.totalPages>0 && page>=data.totalPages){ loadProducts(data.totalPages-1); return; }
        currentPage=data.number; let html='';
        data.content.forEach(p=>{
            const img=p.images?`<img src="${escapeHtml(/^https?:\/\//i.test(p.images) ? p.images : contextPath + (p.images.startsWith('/') ? p.images : '/uploads/' + encodeURIComponent(p.images)))}" style="width:64px;height:64px;object-fit:cover">`:'-';
            html += `<tr><td>${p.productId}</td><td>${img}</td><td>${escapeHtml(p.productName)}</td><td>${escapeHtml(p.category?.categoryName||'')}</td><td>${p.quantity}</td><td>${Number(p.unitPrice).toLocaleString('vi-VN')}</td><td>${p.discount}</td><td>${p.status}</td><td class="text-nowrap"><button class="btn btn-sm btn-warning me-1" onclick="openEditModal(${p.productId})">Sửa</button><button class="btn btn-sm btn-danger" onclick="deleteProduct(${p.productId})">Xóa</button></td></tr>`;
        });
        $('#productBody').html(html||'<tr><td colspan="9" class="text-center">Không có dữ liệu</td></tr>'); renderPagination(data.number,data.totalPages);
    }).fail(xhr=>showAlert(readError(xhr),'danger'));
}
function renderPagination(page,total){ let h=''; for(let i=0;i<total;i++)h+=`<li class="page-item ${i===page?'active':''}"><a class="page-link" href="#" onclick="loadProducts(${i});return false;">${i+1}</a></li>`; $('#pagination').html(h); }
function openAddModal(){ $('#modalTitle').text('Thêm Product'); $('#productForm')[0].reset(); $('#productId').val(''); $('#discount').val(0); $('#status').val(1); modal.show(); }
function openEditModal(id){ $.get(contextPath+'/api/product/getProduct',{id}).done(res=>{ const p=res.body; $('#modalTitle').text('Cập nhật Product'); $('#productForm')[0].reset(); $('#productId').val(p.productId); $('#productName').val(p.productName); $('#quantity').val(p.quantity); $('#unitPrice').val(p.unitPrice); $('#description').val(p.description); $('#discount').val(p.discount); $('#status').val(p.status); $('#categoryId').val(p.category?.categoryId); modal.show(); }).fail(xhr=>showAlert(readError(xhr),'danger')); }
function saveProduct(e){ e.preventDefault(); const id=$('#productId').val(); const fd=new FormData(this); if(id&&!$('#imageFile')[0].files.length)fd.delete('imageFile'); $.ajax({url:contextPath+(id?'/api/product/updateProduct':'/api/product/addProduct'),type:id?'PUT':'POST',data:fd,contentType:false,processData:false}).done(res=>{modal.hide();showAlert(res.message||'Thành công');loadProducts(id?currentPage:0)}).fail(xhr=>showAlert(readError(xhr),'danger')); }
function deleteProduct(id){ if(!confirm('Bạn chắc chắn muốn xóa Product này?'))return; $.ajax({url:contextPath+'/api/product/deleteProduct?productId='+id,type:'DELETE'}).done(res=>{showAlert(res.message||'Đã xóa');loadProducts(currentPage)}).fail(xhr=>showAlert(readError(xhr),'danger')); }
function readError(xhr){try{return JSON.parse(xhr.responseText).message||xhr.responseText}catch(e){return xhr.responseText||'Có lỗi xảy ra'}}
function escapeHtml(text){return $('<div>').text(text||'').html()}
</script>
</body></html>
