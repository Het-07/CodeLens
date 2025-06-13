# Base image
FROM ollama/ollama

# Set working directory
WORKDIR /root

# Copy a script to pull the model
COPY setup.sh /root/setup.sh

# Make the script executable
RUN chmod +x /root/setup.sh

# Run the script to pull tinyllama during build
RUN /root/setup.sh

# Expose the API port
EXPOSE 11434
