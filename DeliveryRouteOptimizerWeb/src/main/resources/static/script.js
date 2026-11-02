const sampleNames = ['Warehouse','Park St','MG Road','Airport','Mall','Station'];
const sampleDistances = [[0,12,10,19,8,15],[12,0,3,7,2,11],[10,3,0,6,20,9],[19,7,6,0,4,5],[8,2,20,4,0,13],[15,11,9,5,13,0]];
let names = [...sampleNames];
let distances = sampleDistances.map(r => [...r]);
const $ = id => document.getElementById(id);

function renderLocations(){
  $('locationCount').textContent = names.length;
  $('locations').innerHTML = names.map((name,i)=>`<div class="location-chip"><span>${i===0?'⌂':'⌖'}</span><input aria-label="Location ${i+1}" maxlength="30" value="${escapeHtml(name)}" data-name="${i}">${i===0?'<span class="start">START</span>':`<button title="Remove location" data-remove="${i}">×</button>`}</div>`).join('');
  $('locations').querySelectorAll('[data-name]').forEach(input=>input.addEventListener('change',e=>{names[+e.target.dataset.name]=e.target.value.trim()||`Location ${+e.target.dataset.name+1}`;renderLocations();}));
  $('locations').querySelectorAll('[data-remove]').forEach(btn=>btn.addEventListener('click',()=>removeLocation(+btn.dataset.remove)));
}
function escapeHtml(s){return String(s).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));}
function renderMatrix(){
  const n=names.length;
  let html='<table class="matrix"><thead><tr><th>KM</th>'+names.map(x=>`<th>${escapeHtml(x)}</th>`).join('')+'</tr></thead><tbody>';
  for(let i=0;i<n;i++){html+=`<tr><th>${escapeHtml(names[i])}</th>`;for(let j=0;j<n;j++){html+=`<td><input type="number" min="0" step="1" value="${distances[i][j]}" data-i="${i}" data-j="${j}" ${i===j?'disabled':''}></td>`;}html+='</tr>';}
  $('matrix').innerHTML=html+'</tbody></table>';
  $('matrix').querySelectorAll('input').forEach(input=>input.addEventListener('change',e=>{
    const i=+e.target.dataset.i,j=+e.target.dataset.j,v=Math.max(0,Math.floor(Number(e.target.value)||0));
    distances[i][j]=v;distances[j][i]=v;renderMatrix();
  }));
}
function refresh(){renderLocations();renderMatrix();$('error').classList.add('hidden');}
function addLocation(){
  if(names.length>=15){showError('Maximum 15 locations supported.');return;}
  names.push(`Location ${names.length+1}`);distances.forEach(row=>row.push(0));distances.push(Array(names.length).fill(0));refresh();
}
function removeLocation(i){if(names.length<=2){showError('At least two locations are required.');return;}names.splice(i,1);distances.splice(i,1);distances.forEach(r=>r.splice(i,1));refresh();}
function showError(message){$('error').textContent=message;$('error').classList.remove('hidden');}
function renderResults(data){
  $('emptyState').classList.add('hidden');$('results').classList.remove('hidden');
  $('bestDistance').textContent=data.bestDistance;$('bestAlgorithm').textContent=`Best result · ${data.bestAlgorithm}`;
  const best=data.results.find(r=>r.distance===data.bestDistance);
  $('routeVisual').innerHTML=best.route.map((stop,i)=>`${i?'<span class="route-arrow">→</span>':''}<span class="route-stop">${escapeHtml(stop)}</span>`).join('');
  $('resultList').innerHTML=data.results.map(r=>`<div class="algorithm-row"><div><b>${escapeHtml(r.algorithm)}</b><small>${r.optimal?'Optimal solution':'Heuristic solution'} · ${r.route.length} route points</small></div><div><strong>${r.distance} km</strong><div class="time">${r.timeMs.toFixed(4)} ms</div></div></div>`).join('');
  const max=Math.max(...data.results.map(r=>r.timeMs),0.0001);
  $('chartEmpty').classList.add('hidden');$('chart').classList.remove('hidden');
  $('chart').innerHTML=data.results.map(r=>`<div class="bar-row"><b>${escapeHtml(r.algorithm)}</b><div class="bar-track"><div class="bar-fill" style="width:${Math.max(1,r.timeMs/max*100)}%"></div></div><span class="bar-value">${r.timeMs.toFixed(4)} ms</span></div>`).join('');
}
async function optimize(){
  $('error').classList.add('hidden');
  if(names.some(n=>!n.trim())){showError('Every location needs a name.');return;}
  const btn=$('optimizeBtn');btn.disabled=true;btn.textContent='Optimizing…';
  try{
    const response=await fetch('/api/optimize',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({names,distances})});
    const data=await response.json();
    if(!response.ok)throw new Error(data.message||data.error||'Unable to optimize this route.');
    renderResults(data);
  }catch(err){showError(err.message||'Could not connect to the optimizer. Make sure the Spring Boot server is running.');}
  finally{btn.disabled=false;btn.innerHTML='Optimize route <span>→</span>';}
}
$('addBtn').addEventListener('click',addLocation);
$('resetBtn').addEventListener('click',()=>{names=[...sampleNames];distances=sampleDistances.map(r=>[...r]);refresh();$('results').classList.add('hidden');$('emptyState').classList.remove('hidden');$('chart').classList.add('hidden');$('chartEmpty').classList.remove('hidden');});
$('optimizeBtn').addEventListener('click',optimize);
refresh();
