import React from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField, MenuItem, Select, FormControl } from "@mui/material";

export default function HazardDialog({ open, onClose, startPos, onSubmit }) {
  const [hazardType, setHazardType] = React.useState("");
  const [description, setDescription] = React.useState("");

  const handleSubmit = () => {
    onSubmit({
      latitude: startPos[0],
      longitude: startPos[1],
      locationDescription: description,
      description: description,
      hazardType: hazardType,
      reportedAt: new Date().toISOString()
    });
    setHazardType("");
    setDescription("");
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth>
      <DialogTitle sx={{ fontWeight: "bold", bgcolor: "#f5f5f5" }}>Report a Hazard</DialogTitle>
      <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, pt: 2 }}>
        <FormControl fullWidth>
          <label>Hazard Type</label>
          <Select value={hazardType} onChange={(e) => setHazardType(e.target.value)}>
            <MenuItem value="Physical">Physical Obstruction</MenuItem>
            <MenuItem value="Environmental">Environmental (Ice, puddles, etc.)</MenuItem>
            <MenuItem value="Infrastructure">Infrastructure Failure (Broken lamp, etc.)</MenuItem>
          </Select>
        </FormControl>
        <TextField
          label="Description" multiline rows={2} fullWidth
          placeholder="Describe the hazard"
          value={description} onChange={(e) => setDescription(e.target.value)}
        />
        <div style={{ padding: 12, background: "#f5f5f5", borderRadius: 6, fontSize: 14, color: "#666" }}>
          <p style={{ margin: 0 }}><strong>Time:</strong> {new Date().toLocaleTimeString()}</p>
          <p style={{ margin: 0 }}><strong>Location:</strong> Current GPS Position</p>
        </div>
      </DialogContent>
      <DialogActions sx={{ p: 2 }}>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleSubmit} variant="contained" color="error" disabled={!hazardType}>
          Submit Report
        </Button>
      </DialogActions>
    </Dialog>
  );
}
