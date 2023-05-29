import socket
import tkinter as tk
from tkinter import messagebox
from tkinter import filedialog

def scan_ports(target_host, start_port, end_port):
    try:
        # Resolve target hostname to IP address
        target_ip = socket.gethostbyname(target_host)

        open_ports = []
        closed_ports = []

        # Scan ports within the specified range
        for port in range(start_port, end_port + 1):
            # Create a socket object
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.settimeout(1)  # Set timeout to 1 second

            # Attempt to connect to the target port
            result = sock.connect_ex((target_ip, port))

            # Check if the port is open or closed
            if result == 0:
                open_ports.append(port)
                output_text.insert(tk.END, f"Port {port}: Open\n")
            else:
                closed_ports.append(port)
                output_text.insert(tk.END, f"Port {port}: Closed\n")

            sock.close()

        output_text.insert(tk.END, f"\n{len(open_ports)} open ports found.")

    except socket.gaierror:
        output_text.insert(tk.END, "Hostname could not be resolved.")

    except socket.error:
        output_text.insert(tk.END, "Could not connect to the server.")

def export_output():
    filename = filedialog.asksaveasfilename(
        initialdir="/",
        title="Export Output",
        filetypes=(("Text Files", "*.txt"), ("All Files", "*.*"))
    )
    if filename:
        with open(filename, "w") as file:
            output = output_text.get("1.0", tk.END).strip()
            file.write(output)
        messagebox.showinfo("Export Successful", "Output exported successfully.")

def clear_output():
    output_text.delete("1.0", tk.END)

def scan_button_click():
    target_host = target_entry.get()
    start_port = int(start_port_entry.get())
    end_port = int(end_port_entry.get())
    output_text.delete("1.0", tk.END)  # Clear previous output
    scan_ports(target_host, start_port, end_port)

# Create the main window
window = tk.Tk()
window.title("Port Scanner")

# Create and configure the input fields
target_label = tk.Label(window, text="Target Hostname/IP:")
target_label.pack()
target_entry = tk.Entry(window)
target_entry.pack()

start_port_label = tk.Label(window, text="Starting Port:")
start_port_label.pack()
start_port_entry = tk.Entry(window)
start_port_entry.pack()

end_port_label = tk.Label(window, text="Ending Port:")
end_port_label.pack()
end_port_entry = tk.Entry(window)
end_port_entry.pack()

# Create and configure the scan button
scan_button = tk.Button(window, text="Scan", command=scan_button_click)
scan_button.pack()

# Create the output box
output_text = tk.Text(window, height=10, width=40)
output_text.pack()

# Create the export output button
export_button = tk.Button(window, text="Export Output", command=export_output)
export_button.pack()

# Create the clear output button
clear_button = tk.Button(window, text="Clear Output", command=clear_output)
clear_button.pack()

# Start the GUI event loop
window.mainloop()
