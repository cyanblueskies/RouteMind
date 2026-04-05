import React from "react";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from "@mui/material";

export default function LoginDialog({ open, onClose, onLogin }) {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <form onSubmit={onLogin}>
        <DialogTitle sx={{ fontWeight: "bold" }}>Login / Sign Up</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus name="username" label="Username"
            fullWidth required variant="outlined" sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2, gap: 1 }}>
          <Button onClick={onClose}>Cancel</Button>
          <Button type="submit" formAction="signup" variant="outlined">Sign Up</Button>
          <Button type="submit" formAction="login" variant="contained" color="primary">Login</Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}
