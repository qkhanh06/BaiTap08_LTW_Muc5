'use strict';
const $ = id => document.getElementById(id);
const mode = $('catalog').dataset.mode;
const productFields = 'productId productName quantity unitPrice images description discount status category { categoryId categoryName }';
const categoryFields = 'categoryId categoryName icon';
let page = 0, totalPages = 0, requestVersion = 0;
const money = value => new Intl.NumberFormat('vi-VN', {style:'currency', currency:'VND'}).format(value);
function message(value) { $('message').textContent = value; }
async function graphql(query, variables = {}) {
    const response = await fetch(document.body.dataset.endpoint, {
        method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({query, variables})
    });
    if (!response.ok) throw new Error('Yêu cầu thất bại (HTTP ' + response.status + ').');
    const result = await response.json();
    if (result.errors?.length) throw new Error(result.errors.map(error => error.message).join('\n'));
    return result.data;
}
function element(tag, text) { const node = document.createElement(tag); node.textContent = text; return node; }
function productImage(product, thumbnail = false) {
    const img = document.createElement('img');
    const context = document.body.dataset.endpoint.replace(/\/graphql\/?$/, '');
    const fallback = context + '/images/products/placeholder.svg';
    const value = (product.images || '').trim();
    img.src = /^https?:\/\//i.test(value) ? value
        : value.startsWith('/') && !value.startsWith('//') ? context + value
        : value && !value.includes('/') && !value.includes(':') ? context + '/uploads/' + encodeURIComponent(value)
        : fallback;
    img.alt = product.productName;
    img.className = thumbnail ? 'product-thumbnail' : 'product-image';
    img.loading = 'lazy';
    img.onerror = () => { img.onerror = null; img.src = fallback; };
    return img;
}
async function loadCategories(select, includeAll = false) {
    const data = await graphql('{ allCategories { categoryId categoryName } }');
    const previous = select.value;
    select.replaceChildren();
    select.add(new Option(includeAll ? 'Tất cả danh mục' : 'Chọn danh mục', ''));
    data.allCategories.forEach(category => select.add(new Option(category.categoryName, category.categoryId)));
    if ([...select.options].some(option => option.value === previous)) select.value = previous;
}
async function loadHome() {
    const version = ++requestVersion;
    message('Đang tải...');
    try {
        const data = await graphql('query($categoryId: ID) { homeProducts(categoryId:$categoryId) { ' + productFields + ' } }',
            {categoryId: $('category-filter').value || null});
        if (version !== requestVersion) return;
        $('cards').replaceChildren();
        data.homeProducts.forEach(product => {
            const card = element('article', ''); card.className = 'card';
            const price = element('p', money(product.unitPrice)); price.className = 'price';
            card.append(productImage(product), element('h2', product.productName), element('p', product.category.categoryName),
                price, element('p', product.description), element('p', 'Số lượng: ' + product.quantity));
            $('cards').append(card);
        });
        $('result-count').textContent = data.homeProducts.length + ' sản phẩm';
        message(data.homeProducts.length ? '' : 'Không có sản phẩm trong danh mục này.');
    } catch(error) { if (version === requestVersion) message(error.message); }
}
async function loadPage() {
    const version = ++requestVersion;
    message('Đang tải...');
    $('previous').disabled = $('next').disabled = true;
    try {
        const fields = mode === 'products' ? productFields : categoryFields;
        const data = await graphql('query($keyword:String,$page:Int,$size:Int) { ' + mode +
            '(keyword:$keyword,page:$page,size:$size) { content { ' + fields + ' } totalElements totalPages number size } }',
            {keyword:$('keyword').value, page, size:Number($('size').value)});
        if (version !== requestVersion) return;
        const result = data[mode];
        totalPages = result.totalPages;
        if (page > 0 && page >= totalPages) { page = Math.max(0,totalPages - 1); return loadPage(); }
        $('rows').replaceChildren();
        result.content.forEach(item => {
            const row = document.createElement('tr');
            const cells = mode === 'products'
                ? [item.productId,item.productName,money(item.unitPrice),item.quantity,item.category.categoryName]
                : [item.categoryId,item.categoryName,item.icon || ''];
            cells.forEach(value => row.append(element('td',value)));
            if (mode === 'products') row.children[1].prepend(productImage(item, true));
            const actions = document.createElement('td');
            const edit = element('button','Sửa'), remove = element('button','Xóa');
            edit.type = remove.type = 'button';
            edit.onclick = () => editItem(item);
            remove.onclick = () => deleteItem(item, remove);
            actions.append(edit,remove); row.append(actions); $('rows').append(row);
        });
        $('page-info').textContent = 'Trang ' + (totalPages ? page + 1 : 0) + '/' + totalPages + ' — ' + result.totalElements + ' kết quả';
        $('previous').disabled = page <= 0;
        $('next').disabled = page + 1 >= totalPages;
        message(result.content.length ? '' : 'Không tìm thấy dữ liệu.');
    } catch(error) { if (version === requestVersion) message(error.message); }
}
function resetEditor() {
    $('editor').reset(); $('editor').elements.namedItem('id').value = '';
    $('form-title').textContent = mode === 'products' ? 'Thêm sản phẩm' : 'Thêm danh mục';
}
function editItem(item) {
    const form = $('editor');
    resetEditor();
    for (const [key,value] of Object.entries(item)) {
        const field = form.elements.namedItem(key);
        if (field) field.value = value ?? '';
    }
    form.elements.namedItem('id').value = item.productId || item.categoryId;
    if (mode === 'products') form.elements.namedItem('categoryId').value = item.category.categoryId;
    $('form-title').textContent = mode === 'products' ? 'Sửa sản phẩm' : 'Sửa danh mục';
    form.scrollIntoView({behavior:'smooth',block:'center'});
}
async function saveItem(event) {
    event.preventDefault();
    const form = $('editor');
    const input = Object.fromEntries(new FormData(form));
    const id = input.id; delete input.id;
    if (mode === 'products') ['quantity','unitPrice','discount','status'].forEach(key => input[key] = Number(input[key]));
    const type = mode === 'products' ? 'Product' : 'Category';
    const operation = (id ? 'update' : 'create') + type;
    $('save').disabled = true;
    try {
        await graphql('mutation($input:' + type + 'Input!' + (id ? ',$id:ID!' : '') + ') { ' + operation +
            '(input:$input' + (id ? ',id:$id' : '') + ') { ' + (mode === 'products' ? 'productId' : 'categoryId') + ' } }', {input, ...(id ? {id} : {})});
        resetEditor(); if (!id) page = 0; await loadPage();
    } catch(error) { message(error.message); }
    finally { $('save').disabled = false; }
}
async function deleteItem(item, button) {
    const id = item.productId || item.categoryId;
    if (!confirm('Xóa "' + (item.productName || item.categoryName) + '"?')) return;
    button.disabled = true;
    try {
        await graphql('mutation($id:ID!) { delete' + (mode === 'products' ? 'Product' : 'Category') + '(id:$id) }', {id});
        if ($('editor').elements.namedItem('id').value === String(id)) resetEditor();
        await loadPage();
    } catch(error) { message(error.message); button.disabled = false; }
}
async function initialize() {
    try {
        if (mode === 'home') {
            await loadCategories($('category-filter'),true);
            $('category-filter').onchange = loadHome;
            await loadHome();
        } else {
            $('editor').onsubmit = saveItem;
            $('cancel').onclick = resetEditor;
            $('search-form').onsubmit = event => { event.preventDefault(); page = 0; loadPage(); };
            $('size').onchange = () => { page = 0; loadPage(); };
            $('previous').onclick = () => { if (page > 0) { page--; loadPage(); } };
            $('next').onclick = () => { if (page + 1 < totalPages) { page++; loadPage(); } };
            if (mode === 'products') await loadCategories($('editor').elements.namedItem('categoryId'));
            await loadPage();
        }
    } catch(error) { message(error.message); }
}
initialize();
