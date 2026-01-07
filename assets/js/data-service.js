/* ============================================
   SCP HMIS - Data Service Module
   Handles data fetching from Google Sheets API
   ============================================ */

/**
 * Configuration for Housekeeping Data
 */
const HK_CONFIG = {
    // URL Web App Google Apps Script
    GAS_URL: 'https://script.google.com/macros/s/AKfycbyOtUQYPTzDWMzU_QK021ckJyDbt-lRLac7x1cwX0U6e3mcwLor4PC4LKxvJp5s0lzR/exec',
    SHEET_ID: '1_aMJkXZxN49COn9p2a64yNCtegPF1mlhL4M66q9JGfU'
};

/**
 * Utility to convert Google Drive viewing links to direct image links
 * @param {string} url - The original Google Drive URL
 * @returns {string} The direct image URL
 */
function getDirectDriveUrl(url) {
    if (!url) return '';
    if (url.includes('drive.google.com')) {
        const fileId = url.match(/[-\w]{25,}/);
        if (fileId) {
            return `https://lh3.googleusercontent.com/d/${fileId[0]}`;
        }
    }
    return url;
}

/**
 * Fetch housekeeping data from Google Apps Script
 * @returns {Promise<Array>} Array of housekeeping log entries
 */
async function fetchHousekeepingData() {
    try {
        console.log('Fetching housekeeping data from:', HK_CONFIG.GAS_URL);
        const response = await fetch(HK_CONFIG.GAS_URL);
        if (!response.ok) {
            console.error('Failed to fetch housekeeping data:', response.status);
            return [];
        }

        const data = await response.json();
        console.log('Data received from GAS:', data);

        if (data && data.length > 1) {
            // Remove header row, take last 10 entries and reverse them (newest first)
            return data.slice(1)
                .slice(-10)
                .reverse()
                .map(v => {
                    // Detect which column might contain the image URL (usually index 4 or 7)
                    let photoUrl = '';
                    if (v[4] && v[4].toString().includes('drive.google.com')) photoUrl = v[4];
                    else if (v[7] && v[7].toString().includes('drive.google.com')) photoUrl = v[7];
                    else if (v[7]) photoUrl = v[7]; // Fallback to index 7 if something is there

                    return {
                        time: v[0] || '---',
                        staff: v[1] || '---',
                        loc: v[2] || '---',
                        status: v[3] || 'Hoàn tất',
                        photo: getDirectDriveUrl(photoUrl),
                        note: v[5] || v[8] || '' // Check index 5 (Ghi chú sự cố) or index 8
                    };
                });
        }

        return [];
    } catch (error) {
        console.error('Error fetching housekeeping data. Possible cause: GAS doGet() not returning JSON.', error);
        return [];
    }
}

/**
 * Start the housekeeping data flow
 * @param {Object} alpineData - Alpine.js data object
 */
function startHKFlow(alpineData) {
    const updateHK = async () => {
        const logs = await fetchHousekeepingData();

        if (logs.length > 0) {
            alpineData.housekeepingLogs = logs;
            // Format first log time for display
            try {
                const firstTime = new Date(logs[0].time);
                alpineData.lastHkUpdate = firstTime.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
            } catch (e) {
                alpineData.lastHkUpdate = logs[0].time;
            }
        }
    };

    // Initial fetch
    updateHK();

    // Update every 30 seconds
    setInterval(updateHK, 30000);
}

// Make function available globally
window.startHKFlow = startHKFlow;
