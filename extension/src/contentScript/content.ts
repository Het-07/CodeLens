import { SummaryPopup } from '../components/SummaryPopup/SummaryPopup.js';
import { STORAGE_KEYS } from '../constants/constants';
import { getFromLocalStorage, setToLocalStorage } from '../utils/utils.function';

(() => {
    let summaryButton: HTMLButtonElement | null = null;
    document.addEventListener("mouseup", async (e) => {

        const response = await getFromLocalStorage([STORAGE_KEYS.USERID, STORAGE_KEYS.TOKEN]);

        if(!response.userId || !response.token) {
            return;
        }

      const selection = window.getSelection();
      const selectedText = selection.toString();
      const sanitizedText = sanitizeSelectedText(selectedText);
      const selectedTextLength = selectedText.length;
     
        if(summaryButton && !summaryButton.contains(e.target as Node)) {
            removeSummaryButton();
        }

      if (selectedTextLength == 0) {
        removeSummaryButton();
        return;
      }
  
      const range = selection.getRangeAt(0);
      const rect = range.getBoundingClientRect();
  
      showSummaryButton(rect, sanitizedText , selectedTextLength);
    });

     // Function to sanitize selected text
     function sanitizeSelectedText(text: string): string {
        // Trim whitespace
        text = text.trim();
        
        // Handle code blocks by preserving line breaks but escaping special characters
        // Replace sequences of spaces with a single space
        text = text.replace(/\s+/g, ' ');
        
        // Escape any special characters that might cause JSON parsing issues
        text = text.replace(/\\/g, '\\\\')  // Escape backslashes
                   .replace(/"/g, '\\"')    // Escape double quotes
                   .replace(/\n/g, '\\n')   // Replace newlines with \n
                   .replace(/\r/g, '\\r')   // Replace carriage returns
                   .replace(/\t/g, '\\t');  // Replace tabs
                   
        return text;
    }

  
    function showSummaryButton(rect: DOMRect, selectedText: string, selectedTextLength: number) {
        if (summaryButton != null) {
            removeSummaryButton(); // Remove previous button if it exists
        }
  
        summaryButton = document.createElement("button");
        styleSummaryButton(rect);

        let viewportWidth = window.innerWidth;
        let viewportHeight = window.innerHeight;

        // Ensure the button stays within the viewport horizontally
        if (rect.right + summaryButton.offsetWidth > viewportWidth) {
            summaryButton.style.left = `${viewportWidth - summaryButton.offsetWidth}px`;
        }

        if (rect.left < 0) {
            summaryButton.style.left = "0px";
        }
  
        // Ensure the button stays within the viewport vertically
        if (rect.bottom + summaryButton.offsetHeight > viewportHeight) {
            summaryButton.style.top = `${viewportHeight - summaryButton.offsetHeight}px`;
        }
        
        summaryButton.addEventListener("mouseup", (e) => {
            e.stopPropagation();
            let popup = SummaryPopup.getInstance(selectedText , selectedTextLength);
  
            const { popupX, popupY } = calculatePopUpPosition(rect);
            setToLocalStorage({ selectedText });
                       
            if (summaryButton) {
                summaryButton.remove(); // Remove the stored summaryButton
                summaryButton = null; // Reset the variable
            }

            popup.showAt(popupX, popupY);
        });
  
        document.body.appendChild(summaryButton);
    }
  
    function removeSummaryButton() {
      if (summaryButton) {
        summaryButton.remove();
        summaryButton = null;
      }
    }

    function styleSummaryButton(rect: DOMRect) {
        if(summaryButton) {
            // Create an <img> element for the logo
            const logoImg = document.createElement("img");
            logoImg.src = chrome.runtime.getURL("/svg/file.svg"); // Replace with your logo path
            logoImg.style.width = "75px"; // Adjust size as needed
            logoImg.style.height = "25px"; // Adjust size as needed
            logoImg.style.verticalAlign = "middle"; // Align vertically

            // Clear existing text and append the logo
            summaryButton.innerHTML = "";
            summaryButton.appendChild(logoImg);

            // Apply styles to the button
            summaryButton.style.position = "absolute";
            summaryButton.style.left = `${rect.left + window.scrollX}px`;
            summaryButton.style.top = `${rect.bottom + window.scrollY + 5}px`;
            summaryButton.style.background = "#EEF5FF";
            summaryButton.style.color = "white";
            summaryButton.style.border = "none";
            summaryButton.style.borderRadius = "5px";
            summaryButton.style.cursor = "pointer";
            summaryButton.style.zIndex = "10000";
            summaryButton.style.display = "flex"; //use flexbox to center the image.
            summaryButton.id = "codelens-summary-button"
            summaryButton.style.alignItems = "center"; //use flexbox to center the image.
            summaryButton.style.justifyContent = "center"; //use flexbox to center the image.
        }
    }

    function calculatePopUpPosition(rect: DOMRect): { popupX: number, popupY: number } {
          // Determine best position for the popup
          let popupWidth = 300;
          let popupHeight = 150;
          let viewportWidth = window.innerWidth;
          let viewportHeight = window.innerHeight;

          // Default positioning: Place the popup to the right and below the selection
          let popupX = rect.right + 10; // Default right
          let popupY = rect.bottom + 10; // Default below

          // Adjust position to prevent overflow
          if (popupX + popupWidth > viewportWidth + window.scrollX) {
              popupX = rect.left - popupWidth - 10; // Move left if it overflows horizontally
          }
          if (popupY + popupHeight > viewportHeight + window.scrollY) {
              popupY = rect.top - popupHeight - 10; // Move up if it overflows vertically
          }
          if (popupX < 0) popupX = 10; // Prevent going off the left
          if (popupY < 0) popupY = 10; // Prevent going off the top

          return { popupX, popupY  }
    }
})();

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.action === "download" && request.base64) {
        const base64String = request.base64;

        // Extract the base64 data (remove prefix if present, e.g., "data:application/...;base64,")
        const base64Data = base64String.includes(",")
            ? base64String.split(",")[1]
            : base64String;

        // Convert base64 to binary
        const byteCharacters = atob(base64Data);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
            byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);

        // Create a blob from the binary data
        const blob = new Blob([byteArray], {
            type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        });

        // Create Download Link (your code)
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "Plugin_download.docx");
        document.body.appendChild(link);
        link.click();

        // Cleanup (your code)
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    }
});