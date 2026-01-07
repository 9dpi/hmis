/**
 * SCP HMIS - Sidebar Synchronization Loader
 * Centralizes the sidebar structure for all dashboard pages.
 */

(function () {
    const isRoot = window.location.pathname.endsWith('hmis.html') || !window.location.pathname.includes('/desktop/');

    // Determine relative paths based on folder depth
    const root = isRoot ? '' : '../../../';
    const desktopPath = isRoot ? 'Archive/Legacy_Web_App/desktop/' : '';
    const showAppPath = isRoot ? 'Archive/Legacy_Web_App/show-app.html' : '../show-app.html';

    const sidebarContent = `
        <div class="logo-frame gap-4">
            <div class="bg-teal-600 w-[56px] h-[56px] flex items-center justify-center rounded-xl shadow-lg text-white text-2xl">
                <i class="fas fa-hospital-user"></i>
            </div>
            <div>
                <h1 class="text-2xl font-black tracking-tighter uppercase leading-none"
                    :class="darkMode ? 'text-white' : 'text-teal-900'">SCP HMIS</h1>
                <p class="text-[9px] opacity-60 uppercase tracking-widest mt-1 font-bold text-teal-600">Medical Center</p>
            </div>
            <button @click="if(typeof mobileMenu !== 'undefined') mobileMenu = false" class="lg:hidden p-2 text-slate-400 hover:text-teal-600">
                <i class="fas fa-times text-xl"></i>
            </button>
        </div>

        <nav class="flex-grow py-4 overflow-y-auto custom-scroll min-h-0">
            <div class="px-6 mb-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">Điều hướng</div>
            <a href="https://9dpi.github.io/SCP/" class="nav-link flex items-center px-6 py-3 no-underline">
                <i class="fas fa-home mr-3 w-5 text-lg text-blue-500"></i> Hub Trung tâm (SCP Hub)
            </a>
            
            <div class="px-6 mt-4 mb-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">Hệ thống quản trị</div>
            
            ${isRoot ? `
            <div @click="section = 'overview'; showAudit = false" :class="section === 'overview' ? 'active' : ''"
                class="nav-link flex items-center px-6 py-4 cursor-pointer">
                <i class="fas fa-chart-line mr-3 w-5 text-lg"></i> Quản lý bệnh viện
            </div>
            ` : `
            <a href="${root}hmis.html" class="nav-link flex items-center px-6 py-4">
                <i class="fas fa-chart-line mr-3 w-5 text-lg"></i> Quản lý bệnh viện
            </a>
            `}

            <a href="${desktopPath}facility.html" class="nav-link flex items-center px-6 py-4 ${window.location.pathname.includes('facility.html') ? 'active' : ''}">
                <i class="fas fa-layer-group mr-3 w-5 text-lg text-orange-500"></i> Quản lý Cơ sở vật chất 
                <span class="ml-auto bg-orange-500 text-[8px] text-white px-1.5 py-0.5 rounded-full font-bold">3D LAB</span>
            </a>
            <a href="${desktopPath}audit.html" class="nav-link flex items-center px-6 py-4 ${window.location.pathname.includes('audit.html') ? 'active' : ''}">
                <i class="fas fa-clipboard-check mr-3 w-5 text-lg text-teal-500"></i> 83 Tiêu chí Chất lượng
            </a>
            <a href="${showAppPath}" target="_blank" class="nav-link flex items-center px-6 py-4 no-underline">
                <i class="fas fa-mobile-alt mr-3 w-5 text-lg"></i> Ứng dụng Mobile App
            </a>

            <div class="px-6 mt-6 mb-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">Khu vực chuyên môn</div>
            <a href="${desktopPath}patients.html" class="nav-link flex items-center px-6 py-3 ${window.location.pathname.includes('patients.html') ? 'active' : ''}">
                <i class="fas fa-user-injured mr-3 w-5"></i> Danh sách bệnh nhân
            </a>
            <a href="${desktopPath}bed-map.html" class="nav-link flex items-center px-6 py-3 ${window.location.pathname.includes('bed-map.html') ? 'active' : ''}">
                <i class="fas fa-procedures mr-3 w-5"></i> Sơ đồ giường bệnh
            </a>
            <a href="${desktopPath}lab-tests.html" class="nav-link flex items-center px-6 py-3 ${window.location.pathname.includes('lab-tests.html') ? 'active' : ''}">
                <i class="fas fa-microscope mr-3 w-5"></i> Xét nghiệm & CĐHA
            </a>
            <a href="${desktopPath}security.html" class="nav-link flex items-center px-6 py-3 ${window.location.pathname.includes('security.html') ? 'active' : ''}">
                <i class="fas fa-shield-alt mr-3 w-5 text-red-500"></i> An ninh Bảo vệ
            </a>

            <div class="px-6 mt-6 mb-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">Vận hành & Hậu cần</div>
            <a href="${desktopPath}pharmacy.html" class="nav-link flex items-center px-6 py-3 ${window.location.pathname.includes('pharmacy.html') ? 'active' : ''}">
                <i class="fas fa-pills mr-3 w-5"></i> Quản lý kho dược
            </a>
            <a href="${desktopPath}billing.html" class="nav-link flex items-center px-6 py-3 ${window.location.pathname.includes('billing.html') ? 'active' : ''}">
                <i class="fas fa-file-invoice-dollar mr-3 w-5"></i> Viện phí & BHYT
            </a>
            <a href="${desktopPath}housekeeping.html" class="nav-link flex items-center px-6 py-3 ${window.location.pathname.includes('housekeeping.html') ? 'active' : ''}">
                <i class="fas fa-broom mr-3 w-5"></i> Vệ sinh
            </a>
        </nav>

        <div class="p-6 border-t flex items-center justify-between transition-colors"
            :class="darkMode ? 'border-slate-800' : 'border-teal-100'">
            <div class="flex items-center gap-3">
                <div class="status-dot"></div>
                <span class="text-[10px] font-bold uppercase tracking-widest"
                    :class="darkMode ? 'text-slate-400' : 'text-teal-700'">Hệ thống sẵn sàng</span>
            </div>
            <button @click="darkMode = !darkMode; if(typeof updateCharts === 'function') updateCharts()"
                class="p-2 bg-teal-800 dark:bg-slate-700 text-teal-100 rounded-lg hover:rotate-12 transition-all">
                <i :class="darkMode ? 'fas fa-sun' : 'fas fa-moon'"></i>
            </button>
        </div>
    `;

    // Wait for DOM
    document.addEventListener('DOMContentLoaded', () => {
        const sidebarEl = document.querySelector('.sidebar');
        if (sidebarEl) {
            sidebarEl.innerHTML = sidebarContent;
            // Ensure sidebar is full height and flex
            sidebarEl.classList.add('flex', 'flex-col', 'h-full');
            console.log('Sidebar synchronized.');
        }
    });
})();
