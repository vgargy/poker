import {
  Dropdown
} from "rsuite";

export default function MenuItem({name, label, icon, action, disabled}) {

    return(
        <>
        <Dropdown.Item 
          disabled = {disabled}
          eventKey={name} 
          onClick={() => {
            if (!disabled && action) {
              action();
            }
          }}
          {...(icon ? { icon } : {})}
          >{label}
        </Dropdown.Item>
        </>
    )
}