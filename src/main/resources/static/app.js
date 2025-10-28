async function getCsrf() {
  const tokenMeta = document.querySelector('meta[name="_csrf"]');
  const headerMeta = document.querySelector('meta[name="_csrf_header"]');
  return {
    header: headerMeta ? headerMeta.getAttribute('content') : null,
    token: tokenMeta ? tokenMeta.getAttribute('content') : null
  };
}

// Attendance page
if (document.getElementById('attendanceForm')) {
  const path = window.location.pathname.split('/');
  const canteenId = path[2];
  async function loadWorkers() {
    try {
      const res = await fetch('/api/workers');
      if (!res.ok) return;
      const workers = await res.json();
      const sel = document.getElementById('workerSelect');
      sel.innerHTML = '<option value="">-- Select worker --</option>';
      workers.forEach(w => {
        const o = document.createElement('option'); o.value = w.id; o.textContent = w.name + ' (' + (w.role || '') + ')'; sel.appendChild(o);
      });
    } catch (err) { console.warn('failed loading workers', err); }
  }

  // Render worker management list/form if present
  async function renderWorkerManagement() {
    const container = document.getElementById('workerList');
    const form = document.getElementById('workerForm');
    if (!container || !form) return;
    try {
      const res = await fetch('/api/workers');
      if (!res.ok) { container.textContent = '(failed to load)'; return; }
      const list = await res.json();
      container.innerHTML = '';
      list.forEach(w => {
        const row = document.createElement('div'); row.className = 'list-row';
        const left = document.createElement('div'); left.textContent = (w.name || '') + ' — ' + (w.role || '');
        const right = document.createElement('div');
        const edit = document.createElement('button'); edit.className = 'btn'; edit.textContent = 'Edit';
        edit.addEventListener('click', () => {
          document.getElementById('workerId').value = w.id;
          document.getElementById('workerName').value = w.name || '';
          document.getElementById('workerRole').value = w.role || '';
        });
        const del = document.createElement('button'); del.className = 'btn'; del.textContent = 'Delete';
        del.addEventListener('click', async () => {
          if (!confirm('Delete worker "' + w.name + '"?')) return;
          const csrf = await getCsrf();
          const headers = {};
          if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
          const r = await fetch('/api/workers/' + w.id, { method: 'DELETE', headers });
          if (r.ok) { renderWorkerManagement(); loadWorkers(); } else alert('Delete failed: '+r.status);
        });
        right.appendChild(edit); right.appendChild(document.createTextNode(' ')); right.appendChild(del);
        row.appendChild(left); row.appendChild(right); container.appendChild(row);
      });
    } catch (err) { console.warn('failed loading worker management', err); }
  }

  // worker form handlers
  if (document.getElementById('workerForm')) {
    document.getElementById('workerForm').addEventListener('submit', async (e) => {
      e.preventDefault();
      const id = document.getElementById('workerId').value;
      const name = document.getElementById('workerName').value;
      const role = document.getElementById('workerRole').value;
      if (!name || !role) { alert('Name and role required'); return; }
      const csrf = await getCsrf();
      const headers = {'Content-Type':'application/json'};
      if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
      const payload = { name, role };
      let res;
      if (id) {
        res = await fetch('/api/workers/' + id, { method: 'PUT', headers, body: JSON.stringify(payload) });
      } else {
        res = await fetch('/api/workers', { method: 'POST', headers, body: JSON.stringify(payload) });
      }
      if (res.ok) {
        document.getElementById('workerForm').reset();
        document.getElementById('workerId').value = '';
        renderWorkerManagement();
        loadWorkers();
      } else {
        alert('Save failed: ' + res.status);
      }
    });

    document.getElementById('cancelWorker').addEventListener('click', (e) => {
      e.preventDefault();
      document.getElementById('workerForm').reset();
      document.getElementById('workerId').value = '';
    });

    // initial render
    renderWorkerManagement();
  }

  async function loadAttendanceList() {
    const res = await fetch(`/api/attendance/recent`);
    if (!res.ok) { document.getElementById('attendanceList').textContent = '(failed to load)'; return; }
    const list = await res.json();
    const container = document.getElementById('attendanceList');
    container.innerHTML = '';
    list.forEach(a => {
      const row = document.createElement('div'); row.className = 'list-row';
      const left = document.createElement('div'); left.textContent = (a.workerName || a.workerId) + ' — ' + new Date(a.date).toLocaleString();
      const right = document.createElement('div'); right.innerHTML = `<span class="badge ${a.status.toLowerCase()}">${a.status}</span>`;
      row.appendChild(left); row.appendChild(right); container.appendChild(row);
    });
  }

  loadWorkers();
  loadAttendanceList();

  document.getElementById('attendanceForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const workerId = document.getElementById('workerSelect').value;
    const status = document.getElementById('status').value;
    const csrf = await getCsrf();
    const headers = {'Content-Type':'application/json'};
    if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
    const res = await fetch('/api/attendance', { method:'POST', headers, body: JSON.stringify({ workerId, status }) });
    if (res.ok) alert('Attendance recorded'); else alert('Failed: '+res.status);
  });
}

// Salary page
if (document.getElementById('markSalaryForm')) {
  async function loadSalaryWorkers() {
    try {
      const res = await fetch('/api/workers');
      if (!res.ok) return;
      const workers = await res.json();
      const sel = document.getElementById('salaryWorkerSelect');
      sel.innerHTML = '<option value="">-- Select worker --</option>';
      workers.forEach(w => { const o = document.createElement('option'); o.value = w.id; o.textContent = w.name + ' (' + (w.role || '') + ')'; sel.appendChild(o); });
    } catch (err) { console.warn('failed loading workers', err); }
  }

  async function renderSalaryList(status) {
    const res = await fetch(`/api/salaries/status/${status}`);
    if (!res.ok) { document.getElementById('salaryList').textContent = '(failed to load)'; return; }
    const list = await res.json();
    const container = document.getElementById('salaryList'); container.innerHTML = '';
    list.forEach(s => {
      const row = document.createElement('div'); row.className = 'list-row';
      const left = document.createElement('div'); left.textContent = (s.workerName || s.workerId) + ' — ' + (s.month || '') + '/' + (s.year || '');
      const right = document.createElement('div'); right.innerHTML = `<span class="badge ${s.status.toLowerCase()}">${s.status}</span>`;
      row.appendChild(left); row.appendChild(right); container.appendChild(row);
    });
  }

  loadSalaryWorkers();
  renderSalaryList('PENDING');

  document.getElementById('markSalaryForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const workerId = document.getElementById('salaryWorkerSelect').value;
    const status = document.getElementById('salaryStatus').value;
    if (!workerId) { alert('Select a worker'); return; }
    const csrf = await getCsrf();
    const headers = {'Content-Type':'application/json'};
    if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
    // Post payload: { workerId, status }
    const res = await fetch(`/api/salaries/mark`, { method:'POST', headers, body: JSON.stringify({ workerId, status }) });
    if (res.ok) alert('Updated'); else alert('Failed: '+res.status);
  });

  document.getElementById('viewPaid').addEventListener('click', async () => {
    renderSalaryList('PAID');
  });
  document.getElementById('viewPending').addEventListener('click', async () => {
    renderSalaryList('PENDING');
  });
}

// Canteens page
if (document.getElementById('canteenList')) {
  async function loadCanteens() {
    const res = await fetch('/api/canteens');
    const list = await res.json();
    const sidebar = document.getElementById('canteenList');
    sidebar.innerHTML = '';
    // also populate the cards area
    const cards = document.getElementById('canteensCards');
    if (cards) cards.innerHTML = '';
    list.forEach((c, idx) => {
      // sidebar numbered button
      const btn = document.createElement('a');
      btn.className = 'canteen-btn';
      btn.href = `/canteen/${c.id}`;
      btn.innerHTML = `<span class="label"><span class="num">${idx+1}</span><span>${c.name}</span></span><span>Open</span>`;
      sidebar.appendChild(btn);

      // cards grid
      if (cards) {
        const card = document.createElement('div'); card.className = 'card';
        const h = document.createElement('h3'); h.textContent = c.name;
        const p = document.createElement('p'); p.textContent = c.location;
        const a = document.createElement('a'); a.className = 'btn action'; a.href = `/canteen/${c.id}`; a.textContent = 'Open';
        // delete button
        const del = document.createElement('button'); del.className = 'btn'; del.textContent = 'Delete';
        del.addEventListener('click', async () => {
          if (!confirm('Delete canteen "' + c.name + '"?')) return;
          try {
            const csrf = await getCsrf();
            const headers = {};
            if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
            const res = await fetch('/api/canteens/' + c.id, { method: 'DELETE', headers });
            if (res.ok) { loadCanteens(); } else { alert('Delete failed: ' + res.status); }
          } catch (err) { console.error('delete failed', err); alert('Delete failed'); }
        });
        card.appendChild(h); card.appendChild(p); card.appendChild(a); card.appendChild(document.createTextNode(' ')); card.appendChild(del);
        cards.appendChild(card);
      }
    });
  }

  document.getElementById('refresh').addEventListener('click', loadCanteens);
  document.getElementById('addCanteenForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('name').value;
    const location = document.getElementById('location').value;
    const price = parseFloat(document.getElementById('price').value);
    const csrf = await getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
    const res = await fetch('/api/canteens', { method: 'POST', headers, body: JSON.stringify({ name, location, defaultPlatePrice: price }) });
    if (res.ok) { alert('Created'); loadCanteens(); } else alert('Failed: ' + res.status);
  });

  loadCanteens();
}

// Food page
if (document.getElementById('purchaseForm')) {
  const path = window.location.pathname.split('/');
  // expected /canteen/{id}/food
  const canteenId = path[2];

  async function refreshInventory() {
    const resInv = await fetch(`/api/canteens/${canteenId}/inventory`);
    if (resInv.ok) {
      const rem = await resInv.json();
      document.getElementById('remaining').textContent = rem;
    }
    const resSales = await fetch('/api/dashboard/stats');
    if (resSales.ok) {
      const stats = await resSales.json();
      document.getElementById('sales').textContent = stats.totalSalesToday;
    }
  }

  document.getElementById('purchaseForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const count = parseInt(document.getElementById('purchaseCount').value || '0', 10);
    const csrf = await getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
    const res = await fetch(`/api/canteens/${canteenId}/purchases`, { method: 'POST', headers, body: JSON.stringify({ platesBought: count }) });
    if (res.ok) { alert('Purchase recorded'); refreshInventory(); } else alert('Failed: ' + res.status);
  });

  document.getElementById('saleForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const count = parseInt(document.getElementById('saleCount').value || '0', 10);
    const csrf = await getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf.token && csrf.header) headers[csrf.header] = csrf.token;
    const res = await fetch(`/api/canteens/${canteenId}/sales`, { method: 'POST', headers, body: JSON.stringify({ platesSold: count }) });
    if (res.ok) { alert('Sale recorded'); refreshInventory(); } else alert('Failed: ' + res.status);
  });

  refreshInventory();
}

// Canteen detail page: show Food / Attendance / Salary buttons
if (document.getElementById('content') && window.location.pathname.match(/^\/canteen\/[^\/]+$/)) {
  (async () => {
    const path = window.location.pathname.split('/');
    const canteenId = path[2];
    try {
      const res = await fetch(`/api/canteens/${canteenId}`);
      if (!res.ok) throw new Error('Failed to load canteen');
      const canteen = await res.json();
      // populate header name if present
      const h = document.querySelector('header h1');
      if (h) h.textContent = canteen.name;

      const content = document.getElementById('content');
      content.innerHTML = '';

      const container = document.createElement('div');
      container.className = 'canteen-options';

      const makeCard = (title, desc, href) => {
        const card = document.createElement('div');
        card.className = 'card option-card';
        const t = document.createElement('h3'); t.textContent = title;
        const p = document.createElement('p'); p.textContent = desc;
        const btn = document.createElement('a'); btn.className = 'btn action'; btn.textContent = title; btn.href = href;
        card.appendChild(t); card.appendChild(p); card.appendChild(btn);
        return card;
      };

      container.appendChild(makeCard('Food', 'Manage plates bought/sold and view daily dashboard', `/canteen/${canteenId}/food`));
      container.appendChild(makeCard('Attendance', 'Record worker in/out times and view attendance', `/canteen/${canteenId}/attendance`));
      container.appendChild(makeCard('Salary', 'Mark salaries paid or unpaid and view lists', `/canteen/${canteenId}/salary`));

      content.appendChild(container);
    } catch (err) {
      console.error(err);
      const content = document.getElementById('content');
      if (content) content.textContent = 'Failed to load canteen details.';
    }
  })();
}
