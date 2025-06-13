const characterLimit = 500;
import { CONFIG } from "../../config/dev.config";
import {
  sendMessageToBackground,
  getFromLocalStorage,
  setToLocalStorage,
} from "../../utils/utils.function";
import { MESSAGE_LISTENER, STORAGE_KEYS } from "../../constants/constants";
export class SummaryPopup {
  static instance = null;

  constructor(summaryText, selectedTextLength) {
    if (SummaryPopup.instance) {
      return SummaryPopup.instance; // Reuse the existing instance
    }

    this.summaryText = summaryText;
    this.selectedTextLength = selectedTextLength;
    this.popup = null;
    SummaryPopup.instance = this; // Store the instance
  }

  createPopup(x, y) {
    if (this.popup) {
      this.popup.remove(); // Remove existing popup if present
    }

    this.popup = document.createElement("div");
    this.popup.id = "custom-popup";

    // Inject Google Fonts dynamically
    const fontLink = document.createElement("link");
    fontLink.rel = "stylesheet";
    fontLink.href =
      "https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap";
    document.head.appendChild(fontLink);

    this.popup.innerHTML = this.getPopupBody();

    // Apply inline styles for the popup container
    this.stylePopup(this.popup, x, y);

    document.body.appendChild(this.popup);

    const redirectionLink = document.getElementById(
      "codelens-redirection-link"
    );

    if (redirectionLink) {
      redirectionLink.addEventListener("mousedown", async (e) => {
        const res = await getFromLocalStorage([STORAGE_KEYS.SELECTED_TEXT]);
        const response = await sendMessageToBackground({
          type: MESSAGE_LISTENER.CREATE_SESSION,
          text: res.selectedText,
        });
        if (response && response.status) {
          this.popup.remove();
          this.popup = null; // Reset popup reference after closing
          window.open(
            `${CONFIG.FRONTEND_URL}/session/${response.response.sessionId}?isRedirected=true`,
            "_blank"
          );
        }
      });
    }

    // Adjust position to keep popup within viewport
    const popupRect = this.popup.getBoundingClientRect();
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    // Ensure the popup stays within the viewport horizontally
    if (popupRect.right > viewportWidth) {
      this.popup.style.left = `${Math.max(
        0,
        viewportWidth - popupRect.width
      )}px`;
    }
    if (popupRect.left < 0) {
      this.popup.style.left = "0px";
    }

    // Ensure the popup stays within the viewport vertically
    if (popupRect.bottom > viewportHeight) {
      this.popup.style.top = `${Math.max(
        0,
        viewportHeight - popupRect.height
      )}px`;
    }
    if (popupRect.top < 0) {
      this.popup.style.top = "0px";
    }

    // Close popup when clicking the SVG icon
    document.getElementById("close-popup")?.addEventListener("click", () => {
      this.popup.remove();
      this.popup = null; // Reset popup reference after closing
    });

    // Cancel button logic
    document.getElementById("cancel-button")?.addEventListener("click", () => {
      this.popup.remove();
      this.popup = null;
    });

    // Download button logic (add your download functionality here)
    document
      .getElementById("download-button")
      ?.addEventListener("click", async () => {
        const summaryTextElement = document.getElementById("summary-text");
        const response = await sendMessageToBackground({
          type: MESSAGE_LISTENER.DOWNLOAD_RESPONSE,
          userPrompt: this.summaryText,
          response: summaryTextElement.innerText,
        });

        if (!response.status) {
          alert(response.message);
        }
      });

    // Save button logic (add your save functionality here)
    document
      .getElementById("save-button")
      ?.addEventListener("click", async () => {
        const summaryTextElement = document.getElementById("summary-text");
        const response = await sendMessageToBackground({
          type: MESSAGE_LISTENER.SAVE_RESPONSE,
          userPrompt: this.summaryText,
          response: summaryTextElement.innerText,
        });

        if (response.status) alert("message saved successfully");
        else alert(response.message);
      });
  }

  showAt(x, y) {
    this.createPopup(x, y);
    this.fetchSummaryText(); // Fetch data and update popup
  }

  // Simulate an asynchronous call to the backend
  async fetchSummaryText() {
    // Simulate an asynchronous call to the backend

    const fetchedTextLength = this.selectedTextLength;

    if (fetchedTextLength > characterLimit) {
      // Selected text length exceeds character limit
      document.getElementById("loader").style.display = "none";
      const characterLimitElement = document.getElementById("character-limit");
      characterLimitElement.style.display = "flex";
      characterLimitElement.style.flexDirection = "column";
      return;
    }

    try {
      // Ensure the text is properly prepared for sending
      const processedText = this.processTextForSending(this.summaryText);

      const response = await sendMessageToBackground({
        type: MESSAGE_LISTENER.SEND_PROMPT,
        text: processedText,
      });

      if (response && response.status) {
        // Hide the loader and display the fetched text
        document.getElementById("loader").style.display = "none";
        const summaryTextElement = document.getElementById("summary-text");
        summaryTextElement.innerText = response.response;
        summaryTextElement.style.display = "block";
        this.enableSaveAndDownloadButton();
      } else {
        // Handle error with more details
        document.getElementById("loader").style.display = "none";
        const summaryTextElement = document.getElementById("summary-text");
        summaryTextElement.innerText =
          response && response.message
            ? `Error: ${response.message}`
            : "Oops! Facing some error processing this content.";
        summaryTextElement.style.display = "block";
        console.error("Summary API Error:", response);
      }
    } catch (error) {
      console.error("Error in fetchSummaryText:", error);
      document.getElementById("loader").style.display = "none";
      const summaryTextElement = document.getElementById("summary-text");
      summaryTextElement.innerText =
        "Unexpected error occurred. Please try again.";
      summaryTextElement.style.display = "block";
    }
  }

  enableSaveAndDownloadButton() {
    // Enable save and download buttons
    const saveButton = document.getElementById("save-button");
    saveButton.disabled = false;
    saveButton.style.cursor = "pointer";
    saveButton.style.opacity = "1";

    const downloadButton = document.getElementById("download-button");
    downloadButton.disabled = false;
    downloadButton.style.cursor = "pointer";
    downloadButton.style.opacity = "1";
  }

  processTextForSending(text) {
    // If text contains code-like content, add a special indicator
    const hasCodeSyntax = /[{}\[\]()<>;:=]/.test(text) && /\n/.test(text);

    if (hasCodeSyntax) {
      // For code content, we might want to wrap it or handle it specially
      return `[CODE_CONTENT]${text}`;
    }

    return text;
  }

  getPopupBody() {
    return `
      <div class="popup-content"
          style="font-family: 'Poppins', sans-serif; display: flex; flex-direction: column; gap: 10px;">
       
        <!-- Header with Summary Title and Close Button -->
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h3 style="font-family: 'Poppins', sans-serif; font-weight: 600; color: #FFFFFF; margin: 0;">Summary</h3>
          <span id="close-popup" style="cursor: pointer; display: flex; align-items: center;">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="20" height="20" style="fill: #FFFFFF;">
              <polygon points="348.071 141.302 260.308 229.065 172.545 141.302 149.917 163.929 237.681 251.692 149.917 339.456 172.545 362.083 260.308 274.32 348.071 362.083 370.699 339.456 282.935 251.692 370.699 163.929 348.071 141.302"/>
              <path d="M425.706,86.294A240,240,0,0,0,86.294,425.706,240,240,0,0,0,425.706,86.294ZM256,464C141.309,464,48,370.691,48,256S141.309,48,256,48s208,93.309,208,208S370.691,464,256,464Z"/>
            </svg>
          </span>
        </div>
 
        <!-- Character Limit Error Message -->
        <div id="character-limit" style="display: none; align-items: center; justify-content: space-evenly; width: 100%; padding: 4px; background-color: #fff3e6; border: 1px solid #ff731d; border-radius: 4px; box-sizing: border-box;">
          <svg height="16" width="16" xmlns="http://www.w3.org/2000/svg" style="margin-right: 5px;">
            <path d="m921-271c-4.423 0-8 3.576-8 8s3.577 8 8 8c4.424 0 8-3.576 8-8s-3.576-8-8-8zm0 1a7 7 0 0 1 7 7 7 7 0 0 1 -7 7 7 7 0 0 1 -7-7 7 7 0 0 1 7-7zm-1 2v4l.5 3h1l.5-3v-4zm1 8a1 1 0 1 0 0 2 1 1 0 0 0 0-2z" fill="#ff731d" transform="translate(-913 271)"/>
          </svg>
          <p style="color: #ff731d; font-size: 9px; font-weight: 500;">Character limit exceeded. Please redirect to our website.</p>
          <div>
            <span style="color: blue; font-size: 9px; font-weight: 500; cursor: pointer;" id="codelens-redirection-link">Click Here</span>
          </div>
        </div>
   
        <!-- Loader -->
        <div id="loader" style="display: flex; align-items: center; justify-content: center; height: 100%; flex-grow: 1;">
          <div style="border: 4px solid rgba(255, 255, 255, 0.3); border-top: 4px solid #FFFFFF; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite;"></div>
        </div>
   
        <!-- Placeholder for summary text -->
        <p id="summary-text" style="display: none; margin: 0; color: #FFFFFF"></p>
 
        <!-- Footer -->
        <div class="popup-footer" style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button id="cancel-button" style="padding: 8px 16px; font-family: 'Poppins', sans-serif; background-color: #F2F2F2; color: #333; border: none; border-radius: 4px; cursor: pointer;">Cancel</button>
          <button id="save-button" disabled style="padding: 8px 16px; font-family: 'Poppins', sans-serif; background-color: #0073BB; color: #FFFFFF; border: none; border-radius: 4px; cursor: not-allowed; opacity: 0.5;">Save</button>
          <button id="download-button" disabled style="padding: 8px; background-color: #0073BB; border: none; border-radius: 4px; cursor: not-allowed; opacity: 0.5;">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="16" height="16" fill="#FFFFFF">
              <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z"/>
            </svg>
          </button>
        </div>
      </div>
 
      <!-- Spinner animation -->
      <style>
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      </style>
    `;
  }

  stylePopup(popup, x, y) {
    // Apply inline styles for the popup container
    popup.style.position = "fixed";
    popup.style.left = `${x}px`;
    popup.style.top = `${y}px`;
    popup.style.width = "300px";
    popup.style.maxHeight = "300px"; // Set max height
    popup.style.overflowY = "auto"; // Add scrollbar if content overflows
    popup.style.background = "#232F3E";
    popup.style.border = "1px solid #ccc";
    popup.style.padding = "15px";
    popup.style.boxShadow = "0px 4px 6px rgba(0,0,0,0.2)";
    popup.style.borderRadius = "8px";
    popup.style.zIndex = "1000";
  }

  // Static method to get or update the summary popup instance
  static getInstance(summaryText, selectedTextLength) {
    if (!SummaryPopup.instance) {
      SummaryPopup.instance = new SummaryPopup(summaryText, selectedTextLength);
    } else {
      SummaryPopup.instance.summaryText = summaryText;
      SummaryPopup.instance.selectedTextLength = selectedTextLength;
    }
    return SummaryPopup.instance;
  }
}
