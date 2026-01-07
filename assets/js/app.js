/* ============================================
   SCP HMIS - Main Application Script
   Initializes and manages the application
   ============================================ */

/**
 * Initialize PanZoom for 3D facility viewer
 */
function initPanZoom() {
    const elem = document.getElementById('panzoom-container');
    if (!elem) return;

    const panzoom = Panzoom(elem, {
        maxScale: 5,
        minScale: 0.5,
        contain: 'outside'
    });

    // Enable zoom with Shift + Mouse Wheel
    elem.parentElement.addEventListener('wheel', (event) => {
        if (!event.shiftKey) return;
        panzoom.zoomWithWheel(event);
    });
}

/**
 * Simulate real-time Housekeeping updates
 */
// startHKFlow removed to avoid conflict with data-service.js

/**
 * Initialize all application components
 */
function initApp() {
    console.log('🏥 SCP HMIS - Initializing...');

    // Initialize charts
    if (typeof initCharts === 'function') {
        initCharts();
        console.log('✅ Charts initialized');
    }

    // Initialize PanZoom for 3D viewer
    if (typeof Panzoom !== 'undefined') {
        initPanZoom();
        console.log('✅ PanZoom initialized');
    }

    console.log('✅ SCP HMIS - Ready!');
}

// Initialize when DOM is loaded
window.addEventListener('load', initApp);

// Export for use in other modules
window.initApp = initApp;
// window.startHKFlow = startHKFlow;
